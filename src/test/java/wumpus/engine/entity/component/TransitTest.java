package wumpus.engine.entity.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test that transit data is present.
 */
public final class TransitTest {

    /**
     * Test that a transit entity may not have a "from".
     */
    @Test
    public void noFrom() {
        final Transit t = new Transit(1L);
        assertTrue(t.getFrom().isEmpty());
    }

    /**
     * Test that a transit entity may have "to" and "from".
     */
    @Test
    public void toAndFrom() {
        final Transit t = new Transit(0L, 1L);
        assertTrue(t.getFrom().isPresent());
        assertEquals(0L, (long) t.getFrom().get());
        assertEquals(1L, t.getTo());
    }
}
