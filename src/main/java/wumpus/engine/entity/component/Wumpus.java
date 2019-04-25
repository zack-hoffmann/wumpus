package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Represents an entity that is a wumpus.
 */
public final class Wumpus extends AbstractEntityComponent {

    /**
     * Initial location of the wumpus.
     */
    private final long location;

    /**
     * Create a wumpus with initial location.
     *
     * @param l
     *              initial location
     */
    public Wumpus(final long l) {
        this.location = l;
    }

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Physical(location), new Hazard(), new Descriptive(
                "a wumpus", "a huge, filthy, smelly, savage wumpus"));
    }
}
