package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Describes a section of the game world.
 */
public final class Zone extends AbstractEntityComponent {

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Container());
    }
}