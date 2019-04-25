package wumpus.engine.entity.component;

/**
 * Marks an entity which exists in the world space.
 */
public final class Physical extends AbstractEntityComponent {

    /**
     * The location in which this entity exists in the world space.
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
     * Retrieve the entity location of the entity in the world.
     *
     * @return the present entity ID of the containing entity
     */
    public long getLocation() {
        return location;
    }
}
