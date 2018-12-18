package wumpus.engine.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Room;

/**
 * Manages "Lairs", the in-game spaces where the player hunts the wumpus.
 *
 * A lair is a container entity holding all of the rooms which comprise the lair
 * space.
 */
public final class LairService {

    /**
     * Maximum random number to generate.
     */
    private static final int MAX_RAND_GEN = 99;

    /**
     * Divisor to use to determine area of grid space to begin generating in.
     */
    private static final int INIT_SPACE_DIVISOR = 3;

    /**
     * The amount of horizontal space used by the lair generation grid.
     */
    private static final int HORIZ_GRID_SPACE = 20;

    /**
     * The amount of vertical space used by the lair generation grid.
     */
    private static final int VERT_GRID_SPACE = 20;

    /**
     * Total number of links headings available.
     */
    private static final int HEADINGS = 4;

    /**
     * Value used to represent north in the lair generation grid.
     */
    private static final int NORTH_HEADING = 0;

    /**
     * Value used to represent east in the lair generation grid.
     */
    private static final int EAST_HEADING = 1;

    /**
     * Value used to represent south in the lair generation grid.
     */
    private static final int SOUTH_HEADING = 2;

    /**
     * Value used to represent west in the lair generation grid.
     */
    private static final int WEST_HEADING = 3;

    /**
     * Default chance to leave a gap in room generation.
     */
    private static final int DEFAULT_GAP_CHANCE = 15;

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * Random percentage (1-100) generator.
     */
    private final Supplier<Integer> random;

    /**
     * Chance (percentage out of 100) that room generation will leave a gap.
     */
    private final int gapChance;

    /**
     * Create a new lair service with a given entity store. This will use the
     * default random number supplier of Java Random seeded with the current
     * millisecond timestamp.
     *
     * @param s
     *              the entity store for this service
     */
    public LairService(final EntityStore s) {
        this.store = s;
        final Random ran = new Random(System.currentTimeMillis());
        this.random = () -> ran.nextInt(MAX_RAND_GEN) + 1;
        this.gapChance = DEFAULT_GAP_CHANCE;
    }

    /**
     * Helper to create and link all of the room entities.
     *
     * @param size
     *                 the number of rooms to be generated in the lair
     *
     * @return the array of IDs for the generated rooms
     */
    private long[] generateRooms(final int size) {
        final Entity[][] grid = new Entity[HORIZ_GRID_SPACE][VERT_GRID_SPACE];
        int x = HORIZ_GRID_SPACE / INIT_SPACE_DIVISOR;
        int y = HORIZ_GRID_SPACE / INIT_SPACE_DIVISOR;
        int c = 0;
        int d = 0;
        int g = gapChance;
        do {
            x = Math.max(Math.min(x, HORIZ_GRID_SPACE - 1), 0);
            y = Math.max(Math.min(y, HORIZ_GRID_SPACE - 1), 0);
            if (grid[x][y] == null) {
                int r = random.get();
                if (r > g) {
                    grid[x][y] = store.create();
                    c++;
                    d++;
                    g = gapChance;
                } else {
                    g = 0;
                }
            } else {
                d--;
            }
            switch (d % HEADINGS) {
            case NORTH_HEADING:
                y--;
                break;
            case EAST_HEADING:
                x++;
                break;
            case SOUTH_HEADING:
                y++;
                break;
            case WEST_HEADING:
                x--;
                break;
            default:
                d *= -1;
                break;
            }

        } while (c < size);

        for (int i = 0; i < HORIZ_GRID_SPACE; i++) {
            for (int j = 0; j < VERT_GRID_SPACE; j++) {
                if (grid[i][j] != null) {
                    final Map<String, Long> l = new HashMap<>();
                    if (j > 0 && grid[i][j - 1] != null) {
                        l.put("North", grid[i][j - 1].getId());
                    }
                    if (i < HORIZ_GRID_SPACE - 1 && grid[i + 1][j] != null) {
                        l.put("East", grid[i + 1][j].getId());
                    }
                    if (j < VERT_GRID_SPACE - 1 && grid[i][j + 1] != null) {
                        l.put("South", grid[i][j + 1].getId());
                    }
                    if (i > 0 && grid[i - 1][j] != null) {
                        l.put("West", grid[i - 1][j].getId());
                    }
                    grid[i][j].registerComponent(new Room(l));
                    store.commit(grid[i][j]);
                }
            }
        }

        return Arrays.stream(grid).flatMap(a -> Arrays.stream(a))
                .filter(Objects::nonNull).mapToLong(Entity::getId).toArray();
    }

    /**
     * Create a new layer with a given number of rooms.
     *
     * @param size
     *                 the number of rooms to be generated in the lair.
     * @return the ID of the generated lair.
     */
    public long createLair(final int size) {
        final Entity lair = store.create();
        lair.registerComponent(new Container(generateRooms(size)));
        store.commit(lair);
        return lair.getId();
    }
}
