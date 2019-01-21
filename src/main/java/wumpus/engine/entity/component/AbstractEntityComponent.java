package wumpus.engine.entity.component;

import java.util.Optional;

import wumpus.engine.entity.Entity;

/**
 * Base implementation of a component which can include a back-reference to its
 * registered entity.
 */
public abstract class AbstractEntityComponent implements Component {

    /**
     * The entity this component is registered to. May be null.
     */
    private Entity e;

    @Override
    public final Optional<Entity> getEntity() {
        return Optional.ofNullable(e);
    }

    /**
     * Set the entity reference for this component.
     *
     * Ideally this should only be set by the registering entity.
     *
     * @param n the entity to register
     */
    public final void setEntity(final Entity n) {
        this.e = n;
    }
}
