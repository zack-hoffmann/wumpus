package wumpus.engine.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import wumpus.engine.entity.component.Component;

/**
 * Testing the memory-based entity store.
 */
public final class MemoryEntityStoreTest {

    /**
     * Tests that new store has no entities.
     */
    @Test
    public void hasNoEntities() {
        final EntityStore es = new MemoryEntityStore();
        assertEquals(0, es.stream().count());
    }

    /**
     * Tests that new store does not have a given entity.
     */
    @Test
    public void doesNotHaveEntity() {
        final EntityStore es = new MemoryEntityStore();
        assertTrue(es.get(0L).isEmpty());
    }

    /**
     * Tests that new store has created entity.
     */
    @Test
    public void hasCreatedEntity() {
        final EntityStore es = new MemoryEntityStore();
        final Entity e = es.create();
        assertTrue(es.get(e.getId()).isPresent());
    }

    /**
     * Tests that the entity stream contains a created entity.
     */
    @Test
    public void hasCreatedEntityInStream() {
        final EntityStore es = new MemoryEntityStore();
        final Entity e = es.create();
        assertTrue(es.stream().anyMatch(n -> n.equals(e)));
    }

    /**
     * Tests that a newly created entity has no components.
     */
    @Test
    public void createdEntityHasNoComponents() {
        final EntityStore es = new MemoryEntityStore();
        assertTrue(es.create().getComponents().isEmpty());
    }

    /**
     * Tests that an entity updated with a component still has that component in
     * the store.
     */
    @Test
    public void committedEntityHasComponent() {
        final EntityStore es = new MemoryEntityStore();
        final Entity e = es.create();
        final Component c = new Component() {
            @Override
            public Entity getEntity() {
                return null;
            }
        };
        e.registerComponent(c);
        es.commit(e);
        final Optional<Entity> n = es.get(e.getId());
        assertTrue(n.isPresent());
        assertTrue(n.get().hasComponent(c.getClass()));
    }
}
