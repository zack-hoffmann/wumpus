package wumpus.engine.service;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import wumpus.Testing;
import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.MemoryEntityStore;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;

/**
 * Testing the player service.
 */
public final class PlayerServiceTest {

    /**
     * Entity store used for testing.
     */
    private EntityStore store;

    /**
     * Create an empty store for each test.
     */
    @Before
    public void initializeEntityStore() {
        store = new MemoryEntityStore();
    }

    /**
     * Verify that generated player ID was committed.
     */
    @Test
    public void playerGenerated() {
        final PlayerService s = new PlayerService(store);
        final Entity player = store.create();
        player.registerComponent(new Listener(m -> {
        }));
        store.commit(player);
        s.tick();
        final Optional<Entity> e = store.get(player.getId());
        Assert.assertTrue(e.isPresent());
    }

    /**
     * Verify that generated player is physical.
     */
    public void playerIsPhysical() {
        final PlayerService s = new PlayerService(store);
        final Entity player = store.create();
        player.registerComponent(new Listener(m -> {
        }));
        store.commit(player);
        s.tick();
        final Optional<Entity> e = store.get(player.getId());
        Assert.assertTrue(e.orElseGet(Testing.badEntitySupplier())
                .hasComponent(Physical.class));
    }

}
