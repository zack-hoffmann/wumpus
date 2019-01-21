package wumpus.engine.entity.component;

import java.util.Optional;

import wumpus.engine.entity.Entity;

/**
 * Denotes an object which may be an aspect of an entity.
 */
public interface Component {

    /**
     * Retrieve a reference to the entity this component is registered to.
     *
     * @return an optional reference to the registering entity
     */
    default Optional<Entity> getEntity() {
        return Optional.empty();
    }

}
