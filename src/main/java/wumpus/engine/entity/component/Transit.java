package wumpus.engine.entity.component;

import java.util.List;

/**
 * Used to describe an entity that is changing locations.
 */
public final class Transit extends AbstractEntityComponent {

    /**
     * Entity which this entity is ariving to.
     */
    private final long to;

    /**
     * Create component with from and to locations.
     *
     * @param t
     *              entity going to
     */
    public Transit(final long t) {
        this.to = t;
    }

    /**
     * Retrieve the location to which the entity is moving.
     *
     * @return entity going to
     */
    public long getTo() {
        return to;
    }

    @Override
    public List<String> debug() {
        return List.of(Long.toString(to));
    }
}
