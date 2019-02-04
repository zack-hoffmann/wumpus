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
                    final long eid = e.getId();
                    final Transit t = m.getByComponent(Transit.class);
                    final Entity toE = store.get(t.getTo()).get();

                    t.getFrom().ifPresent(from -> {
                        final Entity fromE = store.get(from).get();
                        final Container fromC = fromE
                                .getComponent(Container.class);
                        final Predicate<Long> rem = (l -> l != eid);
                        fromE.registerComponent(new Container(fromC, rem));
                        store.commit(fromE);
                    });

                    e.registerComponent(new Physical(t.getTo()));
                    e.deregisterComponent(Transit.class);
                    store.commit(e);

                    final Container toC = toE.getComponent(Container.class);
                    toE.registerComponent(new Container(toC, eid));
                    store.commit(toE);
                });
    }

}
