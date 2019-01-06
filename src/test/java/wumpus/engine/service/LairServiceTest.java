package wumpus.engine.service;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import wumpus.Testing;
import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.MemoryEntityStore;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Room;

/**
 * Testing the lair service to ensure the wumpus lair construction is reliable.
 */
public final class LairServiceTest {

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
     * Verify that generated lair ID was committed.
     */
    @Test
    public void lairGenerated() {
        final int size = 10;
        final LairService s = new LairService(store);
        final Optional<Entity> e = store.get(s.createLair(size));
        Assert.assertTrue(e.isPresent());
    }

    /**
     * Verify that generated lair is a container entity.
     */
    @Test
    public void lairIsContainer() {
        final int size = 10;
        final LairService s = new LairService(store);
        final Optional<Entity> e = store.get(s.createLair(size));
        Assert.assertTrue(e.orElseGet(Testing.badEntitySupplier())
                .hasComponent(Container.class));
    }

    /**
     * Verify that an empty lair can be created.
     */
    @Test
    public void emptyLair() {
        final int size = 0;
        final LairService s = new LairService(store);
        final Optional<Entity> e = store.get(s.createLair(size));
        Assert.assertEquals(size,
                e.get().getComponent(Container.class).getContents().size());
    }

    /**
     * Verify that a very large lair can be created.
     */
    @Test
    public void massiveLair() {
        final int size = 1000;
        final LairService s = new LairService(store);
        final Optional<Entity> e = store.get(s.createLair(size));
        Assert.assertEquals(size,
                e.get().getComponent(Container.class).getContents().size());
    }

    /**
     * Verify that a lair only has rooms.
     */
    @Test
    public void roomsInLair() {
        final int size = 20;
        final LairService s = new LairService(store);
        final Optional<Entity> e = store.get(s.createLair(size));
        final int roomCount = (int) e.orElseGet(Testing.badEntitySupplier())
                .getComponent(Container.class).getContents().stream()
                .map(i -> store.get(i)).filter(o -> o.isPresent())
                .map(o -> o.get()).filter(n -> n.hasComponent(Room.class))
                .count();
        Assert.assertEquals(size, roomCount);
    }
}
