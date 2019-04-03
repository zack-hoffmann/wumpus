package wumpus.engine.entity.component;

import java.util.Map;
import java.util.Set;
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
     * Initialize room based on an existing room with an additional link.
     *
     * @param r
     *              an existing room to extend
     * @param d
     *              the direction of the new link
     * @param i
     *              the ID of the new link
     */
    public Room(final Room r, final Direction d, final long i) {
        final Map<Direction, Long> rooms = r.getLinkedRooms();
        rooms.put(d, i);
        this.linkedRooms = rooms.entrySet().stream().collect(
                Collectors.toConcurrentMap(e -> e.getKey(), e -> e.getValue()));
    }

    /**
     * Initialize room with no links.
     */
    public Room() {
        this(Map.of());
    }

    /**
     * Retrieve linked rooms.
     *
     * @return map of linked rooms
     */
    public Map<Direction, Long> getLinkedRooms() {
        return linkedRooms;
    }

    @Override
    public Set<Component> defaultDepedencies() {
        return Set.of(new Container());
    }
}
