package wumpus.engine.entity.component;

import static org.junit.Assert.assertEquals;
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
        final Lair l = new Lair(1L, 2L);
        assertEquals(1L, l.getEntrace());
    }

    /**
     * Verify that a lair created with a wumpus entity ID has that wumpus.
     */
    @Test
    public void hasWumpus() {
        final Lair l = new Lair(1L, 2L);
        assertEquals(2L, l.getWumpus());
    }
}
