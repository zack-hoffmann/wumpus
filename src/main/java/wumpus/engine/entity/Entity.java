package wumpus.engine.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import wumpus.engine.entity.component.Component;

/**
 * A shallow representation of any game object. This implements a loose ECS
 * architecture and holds no data of its own, instead delegating to components.
 */
public class Entity {

    /**
     * Unique identifier for this entity.
     */
    private final long id;

    /**
     * Components associated with this entity.
     */
    private final Map<Class<? extends Component>, Component> components;

    /**
     * Initialize a new entity with its components.
     *
     * @param i
     *               ID of the new entity
     * @param cs
     *               Components ascribed to this entity
     */
    public Entity(final long i, final Component... cs) {
        this.id = i;
        this.components = Stream.of(cs).collect(
                Collectors.toMap(c -> c.getClass(), Function.identity()));
    }

    /**
     * Observe the ID of this entity.
     *
     * @return entity ID
     */
    public final long getId() {
        return id;
    }

    /**
     * Determine if this entity has a component.
     *
     * @param c
     *              the type of component to find
     * @return true if the entity has the component
     */
    public final boolean hasComponent(final Class<? extends Component> c) {
        return components.keySet().contains(c);
    }

    /**
     * Obtain all components for this entity.
     *
     * @return actual components for this entity.
     */
    public final Set<Component> getComponents() {
        return new HashSet<>(components.values());
    }

    /**
     * Query the entity for component data.
     *
     * @param   <C>
     *              The type of component to retrieve.
     * @param c
     *              The type of component to retrieve.
     * @return The selected component if present.
     */
    public final <C extends Component> C getComponent(final Class<C> c) {
        return Optional.ofNullable(components.get(c)).map(o -> c.cast(o))
                .orElseThrow();
    }

    /**
     * Register a new component with this entity.
     *
     * @param component
     *                      the component to register
     */
    public final void registerComponent(final Component component) {
        components.put(component.getClass(), component);
    }

    /**
     * Remove the association of a component from this entity.
     *
     * @param c
     *              the component type to de-register
     */
    public final void deregisterComponent(final Class<? extends Component> c) {
        components.remove(c);
    }

    @Override
    public final boolean equals(final Object o) {
        return o instanceof Entity && ((Entity) o).id == id;
    }

    @Override
    public final int hashCode() {
        return Long.valueOf(id).hashCode();
    }

}
