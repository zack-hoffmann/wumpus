package wumpus.engine.service;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Cooldown;

/**
 * Service to tick down cooldowns.
 */
public final class CooldownService implements Service {

    /**
     * Execution priority of this service.
     */
    private static final int PRIORITY = 4;

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * Create a service for the given entity store.
     *
     * @param s
     *              the entity store used
     */
    public CooldownService(final EntityStore s) {
        this.store = s;
    }

    @Override
    public void tick() {
        store.stream().component(Cooldown.class).forEach(c -> {
            final Entity e = c.getEntity();
            e.registerComponent(c.tick());
            if (c.cool()) {
                e.deregisterComponent(Cooldown.class);
            }
            store.commit(e);
        });
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
