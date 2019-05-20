package wumpus.engine.service;

import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Logger.
     */
    private static final Logger LOG = Logger
            .getLogger(TransitService.class.getName());

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

                    if (LOG.isLoggable(Level.INFO)) {
                        final Container fromZ = store
                                .get(from.getComponent(Room.class).getZone())
                                .get().getComponent(Container.class);
                        LOG.info(fromZ.debug().toString());
                        final Container toZ = store.get(toR.getZone()).get()
                                .getComponent(Container.class);
                        LOG.info(toZ.debug().toString());
                        fromZ.registerComponent(new Container(fromZ, rem));
                        LOG.info(fromZ.debug().toString());
                        toZ.registerComponent(new Container(toZ, e.getId()));
                        LOG.info(toZ.debug().toString());

                        e.deregisterComponent(Transit.class);

                        LOG.info(from.toString() + from.getId());
                        store.commit(from);
                        // store.commit(fromZ.getEntity());
                        LOG.info(to.toString() + to.getId());
                        store.commit(to);
                        // store.commit(toZ.getEntity());
                        LOG.info(e.toString() + e.getId());
                        store.commit(e);
                    }
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
