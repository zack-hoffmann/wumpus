package wumpus.engine.service;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;

/**
 * Service for the managment of players.
 */
public final class PlayerService {

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
}
