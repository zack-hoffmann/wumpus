package wumpus.engine.entity.component;

import java.util.Optional;
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
    public final Optional<Entity> getEntity() {
        return Optional.ofNullable(entity);
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
        return getEntity().map(e -> e.hasComponent(c)).orElse(false);
    }

    @Override
    public final Set<Component> getComponents() {
        return getEntity().map(e -> e.getComponents()).orElse(Set.of());
    }

    @Override
    public final <C extends Component> C getComponent(final Class<C> c) {
        return getEntity().map(e -> e.getComponent(c)).orElse(null);
    }

    @Override
    public final void registerComponent(final Component component) {
        getEntity().ifPresent(e -> e.registerComponent(component));
    }

    @Override
    public final void deregisterComponent(final Class<? extends Component> c) {
        getEntity().ifPresent(e -> e.deregisterComponent(c));
    }
}
