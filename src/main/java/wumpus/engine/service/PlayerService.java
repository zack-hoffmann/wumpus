package wumpus.engine.service;

import java.util.Optional;
import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Lair;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Transit;
import wumpus.io.IOAdapter;

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
     * @return the id of the new player entity
     */
    public long createPlayer() {
        final Entity e = store.create();
        e.registerComponent(new Player());
        e.registerComponent(new Physical());
        store.commit(e);
        return e.getId();
    }

    /**
     * Associate a player with an I/O-based listener.
     *
     * @param playerId
     *                     entity ID of the player
     * @param io
     *                     I/O adapter for the player
     */
    public void attachPlayer(final long playerId, final IOAdapter io) {
        store.get(playerId).ifPresent(e -> {
            e.registerComponent(new Listener(m -> {
                io.post("\n" + m.toString());
            }));
            store.commit(e);
        });
    }

    /**
     * Disassociate a player from their listener.
     *
     * @param playerId
     *                     entity ID of the player
     */
    public void detachPlayer(final long playerId) {
        store.get(playerId).ifPresent(e -> {
            e.deregisterComponent(Listener.class);
            store.commit(e);
        });
    }

    /**
     * Count the number of active players in the game. This goes by players with
     * listeners attached.
     *
     * @return the number of players active in the game
     */
    public long getAttachedPlayerCount() {
        return store.stream().components(Set.of(Player.class, Listener.class))
                .count();
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

    @Override
    public int priority() {
        return PRIORITY;
    }

}
