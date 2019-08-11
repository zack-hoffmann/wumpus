package wumpus.engine.entity.component;

import java.util.Set;

import wumpus.engine.entity.ComponentRegistry;
import wumpus.engine.entity.Entity;

/**
 * Base implementation of a component which can include a back-reference to its
 * registered entity.
 */
public abstract class AbstractEntityComponent
        implements Component, ComponentRegistry {

    /**
     * The entity this component is registered to. May be null.
     */
    private Entity entity;

    @Override
    public final Entity entity() {
        return entity;
    }

    /**
     * Set the entity reference for this component.
     *
     * Ideally this should only be set by the registering entity.
     *
     * @param n
     *              the entity to register
     */
    public final void setEntity(final Entity n) {
        this.entity = n;
    }

    @Override
    public final boolean hasComponent(final Class<? extends Component> c) {
        return entity.hasComponent(c);
    }

    @Override
    public final Set<Component> components() {
        return entity.components();
    }

    @Override
    public final <C extends Component> C component(final Class<C> c) {
        return entity.component(c);
    }

    @Override
    public final void registerComponent(final Component component) {
        entity.registerComponent(component);
    }

    @Override
    public final void deregisterComponent(final Class<? extends Component> c) {
        entity.deregisterComponent(c);
    }
}
