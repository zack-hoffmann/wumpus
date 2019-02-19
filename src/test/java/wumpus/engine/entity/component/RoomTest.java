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
        final Room r = new Room(new HashMap<Direction, Long>());
        assertTrue(r.getLinkedRooms().isEmpty());
    }

    /**
     * Test that room created with a link has that link.
     */
    @Test
    public void hasLink() {
        final Map<Direction, Long> links = new HashMap<Direction, Long>();
        links.put(TEST_LINK, TEST_ID);
        final Room r = new Room(links);
        assertEquals(TEST_ID, r.getLinkedRooms().get(TEST_LINK));
    }
}
