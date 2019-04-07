package wumpus.engine.entity.component;

import java.util.Set;

/**
 * An arrow entity.
 */
public final class Arrow extends AbstractEntityComponent {

    /**
     * The number of arrows a player should start with.
     */
    private static final int DEFAULT_ARROWS = 3;

    @Override
    public Set<Component> defaultDepedencies() {
        return Set.of(new Descriptive("arrow"), new Item(DEFAULT_ARROWS));
    }
}
