package wumpus.engine.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.ArrowHit;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Descriptive;
import wumpus.engine.entity.component.Lair;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.Transit;
import wumpus.engine.entity.component.Wumpus;
import wumpus.engine.type.Direction;

/**
 * Manages "Lairs", the in-game spaces where the player hunts the wumpus.
 *
 * A lair is a container entity holding all of the rooms which comprise the lair
 * space.
 */
public final class LairService implements Service {

    /**
     * Execution priority of this service.
     */
    public static final int PRIORITY = 80;

    /**
     * Logger.
     */
    private static final Logger LOG = Logger
            .getLogger(LairService.class.getName());

    /**
     * Maximum random number to generate.
     */
    private static final int MAX_RAND_GEN = 99;

    /**
     * Divisor to use to determine area of grid space to begin generating in.
     */
    private static final int INIT_SPACE_DIVISOR = 3;

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
     * Default size of a lair.
     */
    private static final int DEFAULT_SIZE = 20;

    /**
     * The number of rooms required before an additional super bat is
     * introduced.
     */
    private static final int BAT_FACTOR = 15;

    /**
     * The number of rooms required before an additional pit trap is introduced.
     */
    private static final int PIT_FACTOR = 22;

    /**
     * Description of an arrow that has missed.
     */
    private static final String ARROW_MISS = "You hear the disappointing sound"
            + " of a 'clank' as an arrow misses its mark and falls to the "
            + "floor.\n";

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
        final int h = size * 2;
        final int v = size * 2;
        final Entity[][] grid = new Entity[h][v];
        int x = h / INIT_SPACE_DIVISOR;
        int y = v / INIT_SPACE_DIVISOR;
        int c = 0;
        int d = 0;
        int g = gapChance;
        while (c < size) {
            x = Math.max(Math.min(x, h - 1), 0);
            y = Math.max(Math.min(y, v - 1), 0);
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
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < v; j++) {
                if (grid[i][j] != null) {
                    final Map<Direction, Long> l = new HashMap<>();
                    if (j > 0 && grid[i][j - 1] != null) {
                        l.put(Direction.north, grid[i][j - 1].getId());
                    }
                    if (i < h - 1 && grid[i + 1][j] != null) {
                        l.put(Direction.east, grid[i + 1][j].getId());
                    }
                    if (j < v - 1 && grid[i][j + 1] != null) {
                        l.put(Direction.south, grid[i][j + 1].getId());
                    }
                    if (i > 0 && grid[i - 1][j] != null) {
                        l.put(Direction.west, grid[i - 1][j].getId());
                    }
                    grid[i][j].registerComponent(new Room(l));
                    grid[i][j].registerComponent(new Descriptive(
                            "a dark cavern",
                            "This room is lit low by your lantern, but you "
                                    + "cannot see very far."));
                    grid[i][j].registerComponent(new Container());
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
        final HazardService hazards = new HazardService(store);
        final Entity lair = store.create();
        final long[] rooms = generateRooms(size);
        lair.registerComponent(new Container(rooms));
        long firstRoom = -1L;
        long wumpus = -1L;
        if (size > 0) {
            firstRoom = rooms[random.get() % rooms.length];
            if (size > 1) {
                long wumpusRoom;
                do {
                    wumpusRoom = rooms[random.get() % rooms.length];
                } while (wumpusRoom == firstRoom);
                wumpus = hazards.createWumpus(wumpusRoom);
            }
            for (int i = 0; i <= size / BAT_FACTOR; i++) {
                long batRoom;
                do {
                    batRoom = rooms[random.get() % rooms.length];

                } while (batRoom == firstRoom);
                hazards.createBat(batRoom);
            }
            for (int i = 0; i <= size / PIT_FACTOR; i++) {
                long pitRoom;
                do {
                    pitRoom = rooms[random.get() % rooms.length];

                } while (pitRoom == firstRoom);
                hazards.createPitTrap(pitRoom);
            }
        }

        final Entity entrance = store.create();
        entrance.registerComponent(new Room(Map.of(Direction.down, firstRoom)));
        entrance.registerComponent(new Descriptive(
                "the mouth of a cave opening",
                "In the forest floor there is a big hole, with the stench of a"
                        + " wumpus rising from it. "));
        entrance.registerComponent(new Container());
        lair.registerComponent(new Lair(entrance.getId(), wumpus));
        store.commit(lair);
        return lair.getId();
    }

    @Override
    public void tick() {
        final Optional<Lair> defaultLair = store.stream().component(Lair.class)
                .findAny();
        if (defaultLair.isEmpty()) {
            LOG.info("No lair found.  Creating a new default.");
            createLair(DEFAULT_SIZE);
        }

        final StringBuilder exs = new StringBuilder();
        store.stream().components(Set.of(Room.class, ArrowHit.class))
                .forEach(cm -> {
                    exs.append(ARROW_MISS);
                    store.stream().component(Wumpus.class)
                            .filter(w -> !w.hasComponent(Dead.class))
                            .forEach(w -> {
                                final long wLoc = w.getComponent(Physical.class)
                                        .getLocation().getAsLong();
                                final List<Long> ds = store.get(wLoc).get()
                                        .getComponent(Room.class)
                                        .getLinkedRooms().values().stream()
                                        .collect(Collectors.toList());
                                final long wDest = ds
                                        .get(random.get() % ds.size());
                                w.registerComponent(new Transit(wLoc, wDest));
                            });
                    final Entity e = cm.getEntity();
                    e.deregisterComponent(ArrowHit.class);
                    store.commit(e);
                });

        if (exs.length() > 0) {
            store.stream().components(Set.of(Player.class, Listener.class))
                    .map(cm -> cm.getByComponent(Listener.class))
                    .forEach(l -> l.tell(exs.toString()));
        }
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
