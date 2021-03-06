package wumpus.engine.entity.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import wumpus.engine.entity.Entity;

/**
 * Denotes an object which may be an aspect of an entity.
 */
public interface Component {

    /**
     * Retrieve a reference to the entity this component is registered to.
     *
     * @return a reference to the registering entity
     */
    Entity entity();

    /**
     * Get a set of components which an entity with this component must also
     * have. These should only be applied if the entity does not already have
     * these components, regardless of value.
     *
     * @return a set of default dependency components
     */
    default Set<Component> dependencies() {
        return Set.of();
    }

    /**
     * Specify values to return to debug services.
     *
     * @return values to display
     */
    default List<String> debug() {
        return new ArrayList<>();
    }

}
