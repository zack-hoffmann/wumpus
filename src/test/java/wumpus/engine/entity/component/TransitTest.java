package wumpus.engine.entity.component;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test that transit data is present.
 */
public final class TransitTest {

    /**
     * Test that a transit entity has a "to".
     */
    @Test
    public void sendTo() {
        final Transit t = new Transit(1L);
        assertEquals(1L, t.getTo());
    }
}
