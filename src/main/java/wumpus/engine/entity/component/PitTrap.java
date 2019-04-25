package wumpus.engine.entity.component;

import java.util.Set;

/**
 * A pit trap for players to fall in to.
 */
public final class PitTrap extends AbstractEntityComponent {

    /**
     * Initial location of the pit trap.
     */
    private final long location;

    /**
     * Create a pit trap with initial location.
     *
     * @param l
     *              initial location
     */
    public PitTrap(final long l) {
        this.location = l;
    }

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Physical(location), new Hazard(),
                new Descriptive("a pit trap", "a seemingly endless dark pit"));
    }
}
