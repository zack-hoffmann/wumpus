package wumpus.engine.entity;

import static org.junit.Assert.assertEquals;
import java.util.Set;

import org.junit.Test;

import wumpus.Testing;
import wumpus.engine.entity.component.Component;

/**
 * Tests for the entity stream.
 *
 * Excludes testing the stream wrapper methods.
 */
public final class EntityStreamTest {

    /**
     * Tests that the stream has no entities containing a specific component.
     */
    @Test
    public void hasNoComponent() {
        final Entity e = new Entity(1L);
        final EntityStore s = new MemoryEntityStore();
        final EntityStream es = new EntityStream(Set.of(e).stream(), s);
        assertEquals(0L, es.component(Testing.MockComponent.class).count());
        es.close();
    }

    /**
     * Tests that the stream has an entity containing a specific component.
     */
    @Test
    public void hasComponent() {
        final Entity e = new Entity(1L, Testing.getMockComponent());
        final EntityStore s = new MemoryEntityStore();
        final EntityStream es = new EntityStream(Set.of(e).stream(), s);
        assertEquals(1L, es.component(Testing.MockComponent.class).count());
        es.close();
    }

    /**
     * Tests that the stream has a map of components for each entity.
     */
    @Test
    public void hasComponentMap() {
        final Entity e = new Entity(1L, Testing.getMockComponent());
        final EntityStore s = new MemoryEntityStore();
        final EntityStream es = new EntityStream(Set.of(e).stream(), s);
        final Set<Class<? extends Component>> compSet = Set
                .of(Testing.MockComponent.class);
        assertEquals(1L, es.components(compSet).filter(
                m -> m.byComponent(Testing.MockComponent.class) != null)
                .count());
        es.close();
    }
}
