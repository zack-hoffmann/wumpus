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
                    final Entity e = m.entity();
                    final Transit t = m.byComponent(Transit.class);
                    final Entity from = store
                            .get(m.byComponent(Physical.class).location())
                            .get();
                    final Entity to = store.get(t.to()).get();

                    final Container fromC = from.component(Container.class);
                    final Container toC = to.component(Container.class);
                    final Room toR = to.component(Room.class);

                    final Predicate<Long> rem = (l -> l != e.id());
                    from.registerComponent(new Container(fromC, rem));
                    to.registerComponent(new Container(toC, e.id()));

                    e.registerComponent(new Physical(to.id(), toR.zone()));
                    if (e.hasComponent(Player.class)) {
                        e.registerComponent(new Examining(to.id()));
                    }

                    final Container fromZ = store
                            .get(from.component(Room.class).zone()).get()
                            .component(Container.class);
                    final Container toZ = store.get(toR.zone()).get()
                            .component(Container.class);
                    fromZ.registerComponent(new Container(fromZ, rem));
                    toZ.registerComponent(new Container(toZ, e.id()));

                    e.deregisterComponent(Transit.class);

                    store.commit(from);
                    store.commit(fromZ.entity());
                    store.commit(to);
                    store.commit(toZ.entity());
                    store.commit(e);

                    if (e.hasComponent(Wumpus.class)) {
                        wumpusMove(t);
                    }

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
                .map(cm -> cm.byComponent(Listener.class))
                .forEach(l -> l.tell(WUMPUS_MOVE));

        store.get(t.to()).get().component(Room.class).linkedRooms().values()
                .stream().map(id -> store.get(id).get())
                .flatMap(e -> e.contentsStream(store))
                .collect(EntityStream.collector(store))
                .components(Set.of(Player.class, Listener.class))
                .map(cm -> cm.byComponent(Listener.class))
                .forEach(l -> l.tell(WUMPUS_SMELL));

    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
