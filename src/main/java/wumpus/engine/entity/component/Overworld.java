package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Describes the surface world zone.
 */
public final class Overworld extends AbstractEntityComponent {

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Room(this.entity().id()), new Zone());
    }
}
