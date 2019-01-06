package wumpus.engine.entity.component;

import java.util.OptionalLong;

/**
 * Marks an entity which exists in the world space.
 */
public final class Physical implements Component {

    /**
     * The location in which this entity exists in the world space.
     *
     * A value less than or equal to zero here indicates a "stranded" physical
     * entity which may need to be placed somewhere.
     */
    private final long location;

    /**
     * Create a physical component with a location in the world.
     *
     * @param l
     *              the location of the physical entity, typically a container
     */
    public Physical(final long l) {
        location = l;
    }

    /**
     * Create a stranded physical entity (no location).
     *
     * These should be handled by some service to manage stranded physical
     * entities.
     */
    public Physical() {
        this(0);
    }

    /**
     * Retrieve the possible entity location of the entity in the world.
     *
     * @return the present entity ID of the containing entity or empty if
     *         stranded
     */
    public OptionalLong getLocation() {
        if (location > 0) {
            return OptionalLong.of(location);
        } else {
            return OptionalLong.empty();
        }
    }
}
