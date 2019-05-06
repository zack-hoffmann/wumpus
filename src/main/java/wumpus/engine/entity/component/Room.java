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
     * The zone of this room.
     */
    private final long zone;

    /**
     * Initialize room with links.
     *
     * @param l
     *              the linked rooms for this room.
     * @param z
     *              the zone for this room.
     */
    public Room(final Map<Direction, Long> l, final long z) {
        this.linkedRooms = l.entrySet().stream().collect(
                Collectors.toConcurrentMap(e -> e.getKey(), e -> e.getValue()));
        zone = z;
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
        this.zone = r.getZone();
    }

    /**
     * Initialize room with no links.
     *
     * @param z
     *              the zone of the room
     */
    public Room(final long z) {
        this(Map.of(), z);
    }

    /**
     * Retrieve linked rooms.
     *
     * @return map of linked rooms
     */
    public Map<Direction, Long> getLinkedRooms() {
        return linkedRooms;
    }

    /**
     * Retrieve the zone.
     *
     * @return the entity ID of this room's zone.
     */
    public long getZone() {
        return zone;
    }

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Container());
    }
}
