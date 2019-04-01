package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Represents an entity that is a wumpus.
 */
public final class Wumpus extends AbstractEntityComponent {

    @Override
    public Set<Component> defaultDepedencies() {
        return Set.of(new Physical(), new Hazard(), new Descriptive("a wumpus",
                "a huge, filthy, smelly, savage wumpus"));
    }
}
