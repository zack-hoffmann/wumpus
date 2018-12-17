package wumpus.engine.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
     * The amount of horizontal space used by the lair generation grid.
     */
    private static final int HORIZ_GRID_SPACE = 100;

    /**
     * The amount of vertical space used by the lair generation grid.
     */
    private static final int VERT_GRID_SPACE = 100;

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
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * Create a new lair service with a given entity store.
     *
     * @param s
     *              the entity store for this service
     */
    public LairService(final EntityStore s) {
        this.store = s;
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
        int x = HORIZ_GRID_SPACE / 2;
        int y = HORIZ_GRID_SPACE / 2;
        int c = 0;
        do {
            final Entity e = Optional.ofNullable(grid[x][y])
                    .orElse(store.create());
            final Map<String, Long> l = new HashMap<>();

            if (grid[x][y - 1] != null) {
                l.put("North", grid[x][y - 1].getId());
            }
            if (grid[x + 1][y] != null) {
                l.put("East", grid[x + 1][y].getId());
            }
            if (grid[x][y + 1] != null) {
                l.put("South", grid[x][y + 1].getId());
            }
            if (grid[x - 1][y] != null) {
                l.put("West", grid[x - 1][y].getId());
            }
            e.registerComponent(new Room(l));
            store.commit(e);
            grid[x][y] = e;

            switch (c % HEADINGS) {
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
                break;
            }
            c++;
        } while (c < size);

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
