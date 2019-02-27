package wumpus.engine.service;

import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.ArrowHit;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Descriptive;
import wumpus.engine.entity.component.Hazard;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Transit;
import wumpus.engine.entity.component.Wumpus;

/**
 * Servive for managing the lives of wumpuses.
 */
public final class WumpusService implements Service {

    /**
     * Message displayed to a player when a wumpus kills them.
     */
    private static final String DEATH = "The wumpus snaps you up in its "
            + "mighty jaws and quickly puts an end to your life!";

    /**
     * Sound of a wumpus dying.
     */
    private static final String WUMPUS_DEATH = "You hear a satisfying 'thunk' "
            + "as an arrow makes contact with flesh, followed by a beastial "
            + "groan and a thud as a wumpus collapses!";

    /**
     * The entity store for this service.
     */
    private final EntityStore store;

    /**
     * Create this service with the given store.
     *
     * @param s
     *              the entity store to use
     */
    public WumpusService(final EntityStore s) {
        this.store = s;
    }

    /**
     * Create a new wumpus entity at a location.
     *
     * @param location
     *                     the entity ID of the container to put the wumpus in
     * @return the entity ID of the new wumpus
     */
    public long createWumpus(final long location) {
        final Entity wumpus = store.create();
        wumpus.registerComponent(new Wumpus());
        wumpus.registerComponent(new Physical());
        wumpus.registerComponent(new Hazard());
        wumpus.registerComponent(new Transit(location));
        wumpus.registerComponent(new Descriptive("a wumpus",
                "a huge, filthy, smelly, savage wumpus"));
        store.commit(wumpus);
        return wumpus.getId();
    }

    @Override
    public void tick() {
        final StringBuilder exs = new StringBuilder();

        store.stream().components(Set.of(Wumpus.class, ArrowHit.class))
                .filter(m -> !m.getEntity().hasComponent(Dead.class))
                .forEach(m -> {
                    final Entity e = m.getEntity();
                    e.deregisterComponent(ArrowHit.class);
                    e.registerComponent(new Dead());
                    store.commit(e);
                    exs.append(WUMPUS_DEATH);
                });

        store.stream().components(Set.of(Wumpus.class, Physical.class))
                .filter(m -> !m.getEntity().hasComponent(Dead.class))
                .map(m -> m.getByComponent(Physical.class).getLocation())
                .filter(l -> l.isPresent())
                .map(l -> store.get(l.getAsLong()).get()
                        .getComponent(Container.class))
                .forEach(c -> c.getContents().stream()
                        .map(l -> store.get(l).get())
                        .filter(e -> e.hasComponent(Player.class)
                                && e.hasComponent(Listener.class)
                                && !e.hasComponent(Dead.class))
                        .forEach(e -> {
                            e.registerComponent(new Dead());
                            store.commit(e);
                            e.getComponent(Listener.class).tell(DEATH);
                        }));

        if (exs.length() > 0) {
            store.stream().components(Set.of(Player.class, Listener.class))
                    .map(cm -> cm.getByComponent(Listener.class))
                    .forEach(l -> l.tell(exs.toString()));
        }
    }

}
