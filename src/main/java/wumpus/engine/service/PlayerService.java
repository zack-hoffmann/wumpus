package wumpus.engine.service;

import java.util.Optional;
import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Arrow;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Inventory;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Tavern;
import wumpus.engine.entity.component.Transit;

/**
 * Service for the managment of players.
 */
public final class PlayerService implements Service {

    /**
     * Execution priority of this service.
     */
    public static final int PRIORITY = 5;

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * Creates a new player service with the given entity store.
     *
     * @param s
     *              the entity store to be used by this service
     */
    public PlayerService(final EntityStore s) {
        this.store = s;
    }

    /**
     * Create a new player entity.
     *
     * @param e
     *              the entity to associate the player component with
     */
    private void createPlayer(final Entity e) {
        final Entity a = store.create();
        a.registerComponent(new Arrow());
        store.commit(a);
        final Entity i = store.create();
        i.registerComponent(new Inventory());
        i.registerComponent(new Container(a.getId()));
        store.commit(i);
        e.registerComponent(new Player(i.getId()));
        store.commit(e);
    }

    @Override
    public void tick() {
        // Find listeners without players and attach
        store.stream().component(Listener.class)
                .filter(c -> !c.hasComponent(Player.class))
                .forEach(l -> createPlayer(l.getEntity().get()));

        final Optional<Tavern> start = store.stream().component(Tavern.class)
                .findAny();

        if (start.isPresent()) {
            store.stream().components(Set.of(Player.class, Physical.class))
                    .map(m -> m.getByComponent(Physical.class))
                    .filter(p -> p.getLocation().isEmpty())
                    .map(p -> p.getEntity().get())
                    .forEach(e -> e.registerComponent(new Transit(
                            start.get().getEntity().get().getId())));
        }
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
