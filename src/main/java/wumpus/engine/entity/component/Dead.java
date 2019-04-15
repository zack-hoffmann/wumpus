package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Denotes an entity that is deceased.
 */
public final class Dead extends AbstractEntityComponent {

    /**
     * Cooldown tick wait for dying.
     */
    private static final int DEATH_COOLDOWN = 500;

    @Override
    public Set<Component> defaultDepedencies() {
        return Set.of(new Cooldown(DEATH_COOLDOWN));
    }
}
