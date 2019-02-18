package wumpus.engine.entity;

import java.util.Set;

import wumpus.engine.entity.component.Component;

/**
 * A holder (or reference to holder) of components.
 */
public interface ComponentRegistry {

    /**
     * Determine if this container has a component.
     *
     * @param c
     *              the type of component to find
     * @return true if the container has the component
     */
    boolean hasComponent(final Class<? extends Component> c);

    /**
     * Obtain all components for this container.
     *
     * @return actual components for this container.
     */
    Set<Component> getComponents();

    /**
     * Query the entity for component data.
     *
     * @param   <C>
     *              The type of component to retrieve.
     * @param c
     *              The type of component to retrieve.
     * @return The selected component if present.
     */
    <C extends Component> C getComponent(final Class<C> c);

    /**
     * Register a new component with this entity.
     *
     * This will also attempt to register the entity with the component if
     * possible.
     *
     * @param component
     *                      the component to register
     */
    void registerComponent(final Component component);

    /**
     * Remove the association of a component from this entity.
     *
     * @param c
     *              the component type to de-register
     */
    void deregisterComponent(final Class<? extends Component> c);
}
