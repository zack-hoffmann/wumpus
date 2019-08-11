package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Describes a space outside the world to gather illegal entities.
 */
public final class Void extends AbstractEntityComponent {

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Room(this.entity().id()), new Zone());
    }
}
