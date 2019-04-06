package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Storage for character items.
 */
public final class Inventory extends AbstractEntityComponent {

    @Override
    public Set<Component> defaultDepedencies() {
        return Set.of(new Descriptive("the contents of your inventory"),
                new Container());
    }
}
