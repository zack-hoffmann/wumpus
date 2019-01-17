package wumpus.engine.entity.component;

import static org.junit.Assert.assertEquals;
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
        assertEquals(1, c.getContents().size());
    }

    /**
     * Test that a container with multiple entities has those entities.
     */
    @Test
    public void hasMultipleContents() {
        final Container c = new Container(0L, 1L);
        assertTrue(c.getContents().contains(0L));
        assertTrue(c.getContents().contains(1L));
        assertEquals(2, c.getContents().size());
    }

    /**
     * Test that a container can be copied with additional entities.
     */
    @Test
    public void copyContents() {
        final Container c1 = new Container(0L);
        final Container c2 = new Container(c1, 1L);
        assertTrue(c2.getContents().contains(0L));
        assertTrue(c2.getContents().contains(1L));
        assertEquals(2, c2.getContents().size());
    }

    /**
     * Test that a container can be copied with fewer entities.
     */
    @Test
    public void copySubsetContents() {
        final Container c1 = new Container(0L, 1L);
        final Container c2 = new Container(c1, (l -> l != 0L));
        assertFalse(c2.getContents().contains(0L));
        assertTrue(c2.getContents().contains(1L));
        assertEquals(1, c2.getContents().size());
    }
}
