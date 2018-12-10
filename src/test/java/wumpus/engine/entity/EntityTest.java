package wumpus.engine.entity;

import org.junit.Assert;
import org.junit.Test;

import wumpus.engine.entity.component.Component;

/**
 * Testing Entities, including the ID property and the integrity of Component
 * associations.
 */
public final class EntityTest {

    /**
     * Mock component type.
     */
    private static final class MockComponent implements Component {

        /**
         * Arbitrary value for identifying the component.
         */
        private Object value;

        /**
         * No-op.
         */
        private MockComponent() {
        }

        /**
         * Mock component with arbitrary identifying value.
         *
         * @param a
         *              an arbitrary identifying value
         */
        private MockComponent(final Object a) {
            this.value = a;
        }

        @Override
        public boolean equals(final Object x) {
            return x instanceof MockComponent && //
                    value.equals(((MockComponent) x).value);
        }

        @Override
        public int hashCode() {
            return MockComponent.class.getName().hashCode() + value.hashCode();
        }
    }

    /**
     * Entity retains assigned ID.
     */
    @Test
    public void hasId() {
        final long id = 1L;
        final Entity e = new Entity(id);
        Assert.assertEquals(id, e.getId());
    }

    /**
     * Entity reports having an initial component.
     */
    @Test
    public void hasComponent() {
        final Entity e = new Entity(0L, new MockComponent());
        Assert.assertTrue(e.hasComponent(MockComponent.class));
    }

    /**
     * Entity does not have a component when not initially registered.
     */
    @Test
    public void doesNotHaveComponent() {
        final Entity e = new Entity(0L);
        Assert.assertFalse(e.hasComponent(MockComponent.class));
    }

    /**
     * Retrieve an initial component by type with a component property retained.
     */
    @Test
    public void getSameComponent() {
        final Component c = new MockComponent("x");
        final Entity e = new Entity(0L, c);
        Assert.assertEquals(c, e.getComponent(MockComponent.class));
    }

    /**
     * Retrieve all components and ensure initial component is retained.
     */
    @Test
    public void getSameComponentFromSet() {
        final Component c = new MockComponent("x");
        final Entity e = new Entity(0L, c);
        Assert.assertTrue(e.getComponents().contains(c));
    }

    /**
     * Entity reports having a registered component.
     */
    @Test
    public void hasRegisteredComponent() {
        final Entity e = new Entity(0L);
        e.registerComponent(new MockComponent());
        Assert.assertTrue(e.hasComponent(MockComponent.class));
    }

    /**
     * Entity no longer reports having an deregistered component.
     */
    @Test
    public void doesNotHaveDeregisteredComponent() {
        final Entity e = new Entity(0L, new MockComponent());
        e.deregisterComponent(MockComponent.class);
        Assert.assertFalse(e.hasComponent(MockComponent.class));
    }
}
