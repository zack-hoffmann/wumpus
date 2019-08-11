package wumpus.engine.entity.component;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for physical entities.
 */
public final class PhysicalTest {

    /**
     * Test that a container-specified physical has a location.
     */
    @Test
    public void hasLocation() {
        final long zoneId = 2L;
        final long locId = 1L;
        final Physical p = new Physical(locId, zoneId);
        Assert.assertEquals(1L, p.location());
        Assert.assertEquals(2L, p.zone());
    }
}
