package wumpus.engine.entity.component;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for physical entities.
 */
public final class PhysicalTest {

    /**
     * Test that a default physical is stranded.
     */
    @Test
    public void isStranded() {
        final Physical p = new Physical();
        Assert.assertTrue(p.getLocation().isEmpty());
    }

    /**
     * Test that a container-specified physical has a location.
     */
    @Test
    public void hasLocation() {
        final long locId = 1L;
        final Physical p = new Physical(locId);
        Assert.assertEquals(1L, p.getLocation().getAsLong());
    }
}
