package wumpus.engine.entity.component;

/**
 * Describes an entity that is examining another entity.
 */
public final class Examining extends AbstractEntityComponent {

    /**
     * The ID of the entity being examined.
     */
    private final long target;

    /**
     * Creates an examining component.
     *
     * @param t
     *                   the examined entity
     */
    public Examining(final long t) {
        this.target = t;
    }

    /**
     * Get the ID of the entity being examined.
     *
     * @return the examined entity
     */
    public long getTarget() {
        return target;
    }
}
