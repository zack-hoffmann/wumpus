package wumpus.engine.entity.component;

import java.util.Optional;

/**
 * Used to describe an entity that is changing locations.
 */
public final class Transit extends AbstractEntityComponent {

    /**
     * Entity which this entity is leaving from.
     */
    private final Optional<Long> from;

    /**
     * Entity which this entity is ariving to.
     */
    private final long to;

    /**
     * Create component with from and to locations.
     *
     * @param f entity coming from
     * @param t entity going to
     */
    public Transit(final long f, final long t) {
        this.from = Optional.of(f);
        this.to = t;
    }

    /**
     * Create a component with only a to location.
     *
     * Used in cases where the entity is being moved in to the world.
     *
     * @param t entity going to
     */
    public Transit(final long t) {
        this.from = Optional.empty();
        this.to = t;
    }

    /**
     * Retrieve the location form which the entity is moving.
     *
     * @return entity coming from
     */
    public Optional<Long> getFrom() {
        return from;
    }

    /**
     * Retrieve the location to which the entity is moving.
     *
     * @return entity going to
     */
    public long getTo() {
        return to;
    }
}
