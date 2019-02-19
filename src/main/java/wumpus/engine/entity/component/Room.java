package wumpus.engine.entity.component;

import java.util.Map;
import java.util.stream.Collectors;

import wumpus.engine.type.Direction;

/**
 * A travelable room in the game world.
 */
public final class Room extends AbstractEntityComponent {

    /**
     * Rooms linked to this one, by label relative to this room.
     */
    private final Map<Direction, Long> linkedRooms;

    /**
     * Initialize room with links.
     *
     * @param l
     *              the linked rooms for this room.
     */
    public Room(final Map<Direction, Long> l) {
        this.linkedRooms = l.entrySet().stream().collect(
                Collectors.toConcurrentMap(e -> e.getKey(), e -> e.getValue()));
    }

    /**
     * Retrieve linked rooms.
     *
     * @return map of linked rooms
     */
    public Map<Direction, Long> getLinkedRooms() {
        return linkedRooms;
    }
}
