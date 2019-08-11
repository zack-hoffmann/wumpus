package wumpus.engine.entity.component;

import java.util.List;

/**
 * Marks an entity which exists in the world space.
 */
public final class Physical extends AbstractEntityComponent {

    /**
     * The location in which this entity exists in the world space.
     */
    private final long location;

    /**
     * The zone in which this entity exists.
     */
    private final long zone;

    /**
     * Create a physical component with a location in the world.
     *
     * @param l
     *              the location of the physical entity, typically a container
     * @param z
     *              the zone of the physical entity
     */
    public Physical(final long l, final long z) {
        location = l;
        zone = z;
    }

    /**
     * Retrieve the entity location of the entity in the world.
     *
     * @return the present entity ID of the containing entity
     */
    public long location() {
        return location;
    }

    /**
     * Retrieve the zone of the entity in the world.
     *
     * @return the present entity ID of the containing zone
     */
    public long zone() {
        return zone;
    }

    @Override
    public List<String> debug() {
        return List.of(Long.toString(zone), Long.toString(location));
    }
}
