package wumpus.engine.service;

import java.util.Set;
import java.util.function.Predicate;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Transit;

/**
 * Service for moving all entities with a transit component.
 */
public final class TransitService implements Service {

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

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
                    final Entity e = m.getByComponent(Physical.class)
                            .getEntity().get();
                    final Transit t = m.getByComponent(Transit.class);

                    t.getFrom().ifPresent(from -> {
                        removeFromContainer(e, store.get(from).get());
                    });

                    putInContainer(e, store.get(t.getTo()).get());

                    e.deregisterComponent(Transit.class);
                    store.commit(e);

                });
    }

    /**
     * Remove an entity from a container.
     *
     * @param e
     *                 the entity to remove
     * @param from
     *                 the entity of the container to remove from
     */
    private void removeFromContainer(final Entity e, final Entity from) {
        e.registerComponent(new Physical());
        final Container fromC = from.getComponent(Container.class);
        final Predicate<Long> rem = (l -> l != e.getId());
        from.registerComponent(new Container(fromC, rem));
        store.commit(from);

        // TODO if player, describe
    }

    /**
     * Put an entity in a container.
     *
     * @param e
     *               the entity to put
     * @param to
     *               the entity of the container to put in to
     */
    private void putInContainer(final Entity e, final Entity to) {
        e.registerComponent(new Physical(to.getId()));
        final Container toC = to.getComponent(Container.class);
        to.registerComponent(new Container(toC, e.getId()));
        store.commit(to);
        store.commit(e);

        // TODO if player, describe
    }

}
