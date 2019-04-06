package wumpus.engine.entity.component;

import java.util.Set;

/**
 * An arrow entity.
 */
public final class Arrow extends AbstractEntityComponent {

    @Override
    public Set<Component> defaultDepedencies() {
        return Set.of(new Descriptive("arrow"), new Item(1));
    }
}
