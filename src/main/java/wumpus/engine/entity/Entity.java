package wumpus.engine.entity;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import wumpus.engine.entity.component.AbstractEntityComponent;
import wumpus.engine.entity.component.Component;

/**
 * A shallow representation of any game object. This implements a loose ECS
 * architecture and holds no data of its own, instead delegating to components.
 */
public final class Entity implements ComponentRegistry {

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
         * Entity reference.
         */
        private final Entity entity;

        /**
         * Create component map with delegate inner map.
         *
         * @param e
         *              the entity associated with this map
         */
        public ComponentMap(final Entity e) {
            entity = e;
            delegate = new ConcurrentHashMap<Class<? extends Component>, Component>();
        }

        /**
         * Get a component from the map in a typesafe manner.
         *
         * @param c
         *                the type of the component to get
         * @param <C>
         *                the component type
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

        /**
         * Retrieve the entry this map is associated with.
         *
         * @return the entity associated with this map
         */
        public Entity getEntity() {
            return entity;
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
        this.components = new ComponentMap(this);
        Arrays.stream(cs).forEach(c -> {
            this.registerComponent(c);
            this.backRegister(c);
        });
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
     * Get this entity's internal map of components.
     *
     * @return component map for this entity
     */
    public ComponentMap getComponentMap() {
        return components;
    }

    @Override
    public boolean hasComponent(final Class<? extends Component> c) {
        return components.keySet().contains(c);
    }

    @Override
    public Set<Component> getComponents() {
        return new HashSet<>(components.values());
    }

    @Override
    public <C extends Component> C getComponent(final Class<C> c) {
        return components.getByComponent(c);
    }

    @Override
    public void registerComponent(final Component component) {
        components.put(component.getClass(), component);
        component.defaultDepedencies().stream()
                .filter(c -> hasComponent(c.getClass()))
                .forEach(c -> components.put(c.getClass(), c));
        this.backRegister(component);
    }

    @Override
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
