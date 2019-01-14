package wumpus.engine.entity.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Testing the wumpus lair component.
 */
public final class LairTest {

    /**
     * Verify that a lair created with an entrace entity ID has that entrance.
     */
    @Test
    public void hasEntrance() {
        final Lair l = new Lair(1L);
        assertEquals(1L, l.getEntrace());
    }

    /**
     * Verify that a lair created with no entrance entity ID does not have a
     * legal entity ID for its entrance.
     */
    @Test
    public void hasIllegalEntrace() {
        final Lair l = new Lair();
        assertTrue(l.getEntrace() < 0);
    }
}
