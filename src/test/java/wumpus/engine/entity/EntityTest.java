package wumpus.engine.entity;

import org.junit.Assert;
import org.junit.Test;

import wumpus.Testing;
import wumpus.engine.entity.component.Component;

/**
 * Testing Entities, including the ID property and the integrity of Component
 * associations.
 */
public final class EntityTest {

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
        final Entity e = new Entity(0L, Testing.getMockComponent());
        Assert.assertTrue(e.hasComponent(Testing.MockComponent.class));
    }

    /**
     * Entity does not have a component when not initially registered.
     */
    @Test
    public void doesNotHaveComponent() {
        final Entity e = new Entity(0L);
        Assert.assertFalse(e.hasComponent(Testing.MockComponent.class));
    }

    /**
     * Retrieve an initial component by type with a component property retained.
     */
    @Test
    public void getSameComponent() {
        final Component c = Testing.getMockComponent("x");
        final Entity e = new Entity(0L, c);
        Assert.assertEquals(c, e.getComponent(Testing.MockComponent.class));
    }

    /**
     * Retrieve all components and ensure initial component is retained.
     */
    @Test
    public void getSameComponentFromSet() {
        final Component c = Testing.getMockComponent("x");
        final Entity e = new Entity(0L, c);
        Assert.assertTrue(e.getComponents().contains(c));
    }

    /**
     * Entity reports having a registered component.
     */
    @Test
    public void hasRegisteredComponent() {
        final Entity e = new Entity(0L);
        e.registerComponent(Testing.getMockComponent());
        Assert.assertTrue(e.hasComponent(Testing.MockComponent.class));
    }

    /**
     * Entity no longer reports having an deregistered component.
     */
    @Test
    public void doesNotHaveDeregisteredComponent() {
        final Entity e = new Entity(0L, Testing.getMockComponent());
        e.deregisterComponent(Testing.MockComponent.class);
        Assert.assertFalse(e.hasComponent(Testing.MockComponent.class));
    }
}
