package wumpus.engine.type;

import java.util.Arrays;
import java.util.Optional;

/**
 * Game directions.
 */
public enum Direction {

    /**
     * North.
     */
    north,

    /**
     * South.
     */
    south,

    /**
     * East.
     */
    east,

    /**
     * West.
     */
    west,

    /**
     * Up.
     */
    up,

    /**
     * Down.
     */
    down;

    /**
     * Find a direction matching the provided input.
     *
     * @param s
     *              an input describing a direction
     * @return the matching direction optional
     */
    public static Optional<Direction> match(final String s) {
        final String lowers = s.toLowerCase();
        return Arrays.stream(Direction.values()).filter(
                d -> d.name().equals(lowers) || d.shortcut().equals(lowers))
                .findAny();
    }

    /**
     * Gets the one-letter shortcut for the direction.
     *
     * @return the one-letter shortcut
     */
    public String shortcut() {
        return name().substring(0, 1);
    }
}
