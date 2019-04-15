package wumpus.engine.entity.component;

import java.util.List;

/**
 * Indicates how long the entity needs to wait before it can beform an action.
 */
public final class Cooldown extends AbstractEntityComponent {

    /**
     * Number of ticks to wait for cooldown.
     */
    private final int wait;

    /**
     * Create a new cooldown wait.
     *
     * @param w
     *              the number of ticks to wait
     */
    public Cooldown(final int w) {
        this.wait = w;
    }

    /**
     * Return a cooldown with a single less tick to wait.
     *
     * @return the new cooldown with one less wait tick
     */
    public Cooldown tick() {
        return new Cooldown(wait - 1);
    }

    /**
     * Determines if the cooldown wait has passed. In other words, the number of
     * ticks to wait must be zero or lower.
     *
     * @return true if there are no more ticks left to wait, false otherwise
     */
    public boolean cool() {
        return wait <= 0;
    }

    @Override
    public List<String> debug() {
        return List.of(Integer.toString(wait));
    }
}
