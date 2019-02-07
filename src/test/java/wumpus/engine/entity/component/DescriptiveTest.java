package wumpus.engine.entity.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for descriptive component.
 */
public final class DescriptiveTest {

    /**
     * Validate that a single description returns for both options.
     */
    @Test
    public void hasSameDescription() {
        final Descriptive d = new Descriptive("test");
        assertEquals("test", d.getShortDescription());
        assertEquals("test", d.getLongDescription());
    }

    /**
     * Validate two separate descriptions.
     */
    @Test
    public void hasDifferentDescription() {
        final Descriptive d = new Descriptive("test", "long test");
        assertEquals("test", d.getShortDescription());
        assertEquals("long test", d.getLongDescription());
    }
}
