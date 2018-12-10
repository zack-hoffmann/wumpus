package wumpus.engine.entity.component;

import java.util.Map;

/**
 * A travelable room in the game world.
 */
public final class Room implements Component {

    /**
     * Rooms linked to this one, by label relative to this room.
     */
    private final Map<String, Long> linkedRooms;

    /**
     * Initialize room with links.
     *
     * @param l
     *              the linked rooms for this room.
     */
    public Room(final Map<String, Long> l) {
        this.linkedRooms = l;
    }

    /**
     * Retrieve linked rooms.
     *
     * @return map of linked rooms
     */
    public Map<String, Long> getLinkedRooms() {
        return linkedRooms;
    }
}
