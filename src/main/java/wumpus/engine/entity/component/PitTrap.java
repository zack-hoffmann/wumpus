package wumpus.engine.entity.component;

import java.util.Set;

/**
 * A pit trap for players to fall in to.
 */
public final class PitTrap extends AbstractEntityComponent {

    @Override
    public Set<Component> defaultDepedencies() {
        return Set.of(new Physical(), new Hazard(),
                new Descriptive("a pit trap", "a seemingly endless dark pit"));
    }
}
