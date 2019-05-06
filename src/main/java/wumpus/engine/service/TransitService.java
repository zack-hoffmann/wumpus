package wumpus.engine.service;

import java.util.Set;
import java.util.function.Predicate;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.EntityStream;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Examining;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.Transit;
import wumpus.engine.entity.component.Wumpus;

/**
 * Service for moving all entities with a transit component.
 */
public final class TransitService implements Service {

    /**
     * Execution priority of this service.
     */
    private static final int PRIORITY = 10;

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * The smell of a wumpus in the adjacent room.
     */
    private static final String WUMPUS_SMELL = "You smell a wumpus nearby!\n";

    /**
     * The sound of a wumpus moving.
     */
    private static final String WUMPUS_MOVE = "You hear the thunderous sound "
            + "of trampling hooves as a wumpus changes its location.";

    /**
     * Creates a new transit service with the given entity store.
     *
     * @param s
     *              the entity store to be used by this service
     */
    public TransitService(final EntityStore s) {
        this.store = s;
    }

    @Override
    public void tick() {
        store.stream().components(Set.of(Transit.class, Physical.class))
                .forEach(m -> {
                    final Entity e = m.getEntity();
                    final Transit t = m.getByComponent(Transit.class);
                    final Entity from = store
                            .get(m.getByComponent(Physical.class).getLocation())
                            .get();
                    final Entity to = store.get(t.getTo()).get();

                    final Container fromC = from.getComponent(Container.class);
                    final Container toC = to.getComponent(Container.class);
                    final Room toR = to.getComponent(Room.class);

                    final Predicate<Long> rem = (l -> l != e.getId());
                    from.registerComponent(new Container(fromC, rem));
                    to.registerComponent(new Container(toC, e.getId()));

                    e.registerComponent(
                            new Physical(to.getId(), toR.getZone()));
                    if (e.hasComponent(Player.class)) {
                        e.registerComponent(new Examining(to.getId()));
                    }

                    store.commit(from);
                    store.commit(to);
                    store.commit(e);

                    if (e.hasComponent(Wumpus.class)) {
                        wumpusMove(t);
                    }

                    e.deregisterComponent(Transit.class);
                    store.commit(e);

                });
    }

    /**
     * Process special actions for wumpus movement.
     *
     * @param t
     *              the location the wumpus is moving to
     */
    private void wumpusMove(final Transit t) {
        store.stream().components(Set.of(Player.class, Listener.class))
                .map(cm -> cm.getByComponent(Listener.class))
                .forEach(l -> l.tell(WUMPUS_MOVE));

        store.get(t.getTo()).get().getComponent(Room.class).getLinkedRooms()
                .values().stream().map(id -> store.get(id).get())
                .flatMap(e -> e.getComponent(Container.class).getContents()
                        .stream())
                .map(id -> store.get(id).get())
                .collect(EntityStream.collector())
                .components(Set.of(Player.class, Listener.class))
                .map(cm -> cm.getByComponent(Listener.class))
                .forEach(l -> l.tell(WUMPUS_SMELL));

    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
