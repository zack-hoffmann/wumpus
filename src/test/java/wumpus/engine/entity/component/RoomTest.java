package wumpus.engine.entity.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import wumpus.engine.type.Direction;

/**
 * Tests for the world room component.
 */
public final class RoomTest {

    /**
     * Test entity ID.
     */
    private static final Long TEST_ID = 1L;

    /**
     * Test link description.
     */
    private static final Direction TEST_LINK = Direction.up;

    /**
     * Test that room created with no links has no links.
     */
    @Test
    public void hasNoLinks() {
        final long zoneId = 1L;
        final Room r = new Room(new HashMap<Direction, Long>(), zoneId);
        assertTrue(r.getLinkedRooms().isEmpty());
        assertEquals(1L, r.getZone());
    }

    /**
     * Test that room created with a link has that link.
     */
    @Test
    public void hasLink() {
        final long zoneId = 1L;
        final Map<Direction, Long> links = new HashMap<Direction, Long>();
        links.put(TEST_LINK, TEST_ID);
        final Room r = new Room(links, zoneId);
        assertEquals(TEST_ID, r.getLinkedRooms().get(TEST_LINK));
        assertEquals(1L, r.getZone());
    }
}
