package wumpus.engine.entity.component;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Testing the container component.
 */
public final class ContainerTest {

    /**
     * Test that a new container is empty.
     */
    @Test
    public void hasNoContents() {
        final Container c = new Container();
        assertTrue(c.getContents().isEmpty());
    }

    /**
     * Test that a container with an entity is not empty.
     */
    @Test
    public void hasContents() {
        final Container c = new Container(0L);
        assertFalse(c.getContents().isEmpty());
    }
}
