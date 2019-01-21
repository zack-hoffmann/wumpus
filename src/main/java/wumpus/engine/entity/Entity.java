package wumpus.engine.entity;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import wumpus.engine.entity.component.AbstractEntityComponent;
import wumpus.engine.entity.component.Component;

/**
 * A shallow representation of any game object. This implements a loose ECS
 * architecture and holds no data of its own, instead delegating to components.
 */
public final class Entity {

    /**
     * Defines helped methods for retrieving components from a conventional map.
     */
    public static final class ComponentMap
            extends AbstractMap<Class<? extends Component>, Component> {

        /**
         * Wrapped inner map.
         */
        private final Map<Class<? extends Component>, Component> delegate;

        /**
         * Create component map with delegate inner map.
         *
         * @param cs
         *               the map to wrap
         */
        public ComponentMap(
                final Map<Class<? extends Component>, Component> cs) {
            delegate = cs;
        }

        /**
         * Get a component from the map in a typesafe manner.
         *
         * @param c
         *              the type of the component to get
         * @param   <C>
         *              the component type
         * @return the matching component
         */
        public <C extends Component> C getByComponent(final Class<C> c) {
            final Component cand = delegate.get(c);
            if (c.equals(cand.getClass())) {
                return c.cast(cand);
            } else {
                throw new NoSuchElementException(c.getName());
            }
        }

        @Override
        public Set<Entry<Class<? extends Component>, Component>> entrySet() {
            return delegate.entrySet();
        }

        @Override
        public Component put(final Class<? extends Component> key,
                final Component value) {
            return delegate.put(key, value);
        }

    }

    /**
     * Unique identifier for this entity.
     */
    private final long id;

    /**
     * Components associated with this entity.
     */
    private final ComponentMap components;

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
        this.components = new ComponentMap(Stream.of(cs).collect(
                Collectors.toMap(c -> c.getClass(), Function.identity())));
        this.components.values().stream().forEach(c -> this.backRegister(c));
    }

    /**
     * Register this entity with the component if possible.
     *
     * @param c
     *              the component to register
     */
    private void backRegister(final Component c) {
        if (c instanceof AbstractEntityComponent) {
            ((AbstractEntityComponent) c).setEntity(this);
        }
    }

    /**
     * Deregister this entity with the component if possible.
     *
     * @param c
     *              the component to deregister
     */
    private void backDeregister(final Component c) {
        if (c != null && c instanceof AbstractEntityComponent
                && c.getEntity().isPresent()
                && this.equals(c.getEntity().get())) {
            ((AbstractEntityComponent) c).setEntity(null);
        }
    }

    /**
     * Observe the ID of this entity.
     *
     * @return entity ID
     */
    public long getId() {
        return id;
    }

    /**
     * Determine if this entity has a component.
     *
     * @param c
     *              the type of component to find
     * @return true if the entity has the component
     */
    public boolean hasComponent(final Class<? extends Component> c) {
        return components.keySet().contains(c);
    }

    /**
     * Obtain all components for this entity.
     *
     * @return actual components for this entity.
     */
    public Set<Component> getComponents() {
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
    public <C extends Component> C getComponent(final Class<C> c) {
        return components.getByComponent(c);
    }

    /**
     * Register a new component with this entity.
     *
     * This will also attempt to register the entity with the component if
     * possible.
     *
     * @param component
     *                      the component to register
     */
    public void registerComponent(final Component component) {
        components.put(component.getClass(), component);
        this.backRegister(component);
    }

    /**
     * Remove the association of a component from this entity.
     *
     * @param c
     *              the component type to de-register
     */
    public void deregisterComponent(final Class<? extends Component> c) {
        this.backDeregister(components.get(c));
        components.remove(c);
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Entity && ((Entity) o).id == id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

}
