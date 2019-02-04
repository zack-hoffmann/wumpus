package wumpus.engine.service;

import java.util.Optional;
import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Lair;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Transit;

/**
 * Service for the managment of players.
 */
public final class PlayerService implements Service {

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
     * @return the id of the new player entity
     */
    public long createPlayer() {
        final Entity e = store.create();
        e.registerComponent(new Player());
        e.registerComponent(new Physical());
        store.commit(e);
        return e.getId();
    }

    @Override
    public void tick() {
        final Optional<Lair> defaultLair = store.stream().component(Lair.class)
                .findAny();

        // If a default lair is available, move all players with no location to
        // that lair entrance.
        if (defaultLair.isPresent()) {
            store.stream().components(Set.of(Player.class, Physical.class))
                    .map(m -> m.getByComponent(Physical.class))
                    .filter(p -> p.getLocation().isEmpty())
                    .map(p -> p.getEntity().get())
                    .forEach(e -> e.registerComponent(
                            new Transit(defaultLair.get().getEntrace())));
        }
    }
}
