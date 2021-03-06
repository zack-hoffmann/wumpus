package wumpus.engine.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import wumpus.Testing;
import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.MemoryEntityStore;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Lair;
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
        final LairService s = new LairService(store, size);
        s.tick();
        final Optional<Entity> e = store.stream().component(Lair.class)
                .map(c -> c.entity()).findFirst();
        assertTrue(e.isPresent());
    }

    /**
     * Verify that generated entity has the lair component.
     */
    public void isLair() {
        final int size = 10;
        final LairService s = new LairService(store, size);
        s.tick();
        final Optional<Entity> e = store.stream().component(Lair.class)
                .map(c -> c.entity()).findFirst();
        assertTrue(e.orElse(Testing.badEntitySupplier().get())
                .hasComponent(Lair.class));
    }

    /**
     * Verify that non-empty lair has an entrance entity.
     */
    public void lairHasEntrance() {
        final int size = 10;
        final LairService s = new LairService(store, size);
        s.tick();
        final Optional<Entity> e = store.stream().component(Lair.class)
                .map(c -> c.entity()).findFirst();
        assertTrue(e.orElse(Testing.badEntitySupplier().get())
                .component(Lair.class).entrance() >= 0);
    }

    /**
     * Verify that generated lair is a container entity.
     */
    @Test
    public void lairIsContainer() {
        final int size = 10;
        final LairService s = new LairService(store, size);
        s.tick();
        final Optional<Entity> e = store.stream().component(Lair.class)
                .map(c -> c.entity()).findFirst();
        assertTrue(e.orElseGet(Testing.badEntitySupplier())
                .hasComponent(Container.class));
    }

    /**
     * Verify that a very large lair can be created.
     */
    @Test
    public void massiveLair() {
        final int size = 1000;
        final LairService s = new LairService(store, size);
        s.tick();
        final Optional<Entity> e = store.stream().component(Lair.class)
                .map(c -> c.entity()).findFirst();
        assertEquals(size,
                e.get().component(Container.class).contents().stream()
                        .map(l -> store.get(l).get())
                        .filter(n -> n.hasComponent(Room.class)).count());
    }

    /**
     * Verify that a lair only has rooms.
     */
    @Test
    public void roomsInLair() {
        final int size = 20;
        final LairService s = new LairService(store, size);
        s.tick();
        final Optional<Entity> e = store.stream().component(Lair.class)
                .map(c -> c.entity()).findFirst();
        final int roomCount = (int) e.orElseGet(Testing.badEntitySupplier())
                .component(Container.class).contents().stream()
                .map(i -> store.get(i)).filter(o -> o.isPresent())
                .map(o -> o.get()).filter(n -> n.hasComponent(Room.class))
                .count();
        assertEquals(size, roomCount);
    }
}
