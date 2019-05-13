package wumpus.engine.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import wumpus.engine.entity.component.Hidden;
import wumpus.engine.entity.component.Lair;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.PitTrap;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.SuperBat;
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
    private static final int PRIORITY = 80;

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
     * Default size of new lairs.
     */
    private final int defaultSize;

    /**
     * Create a new lair service with a given entity store. This will use the
     * default random number supplier of Java Random seeded with the current
     * millisecond timestamp.
     *
     * @param s
     *               the entity store for this service
     * @param ds
     *               the default lair size
     */
    public LairService(final EntityStore s, final int ds) {
        this.store = s;
        final Random ran = new Random(System.currentTimeMillis());
        this.random = () -> ran.nextInt(MAX_RAND_GEN) + 1;
        this.gapChance = DEFAULT_GAP_CHANCE;
        this.defaultSize = ds;
    }

    /**
     * Helper to create and link all of the room entities.
     *
     * @param size
     *                 the number of rooms to be generated in the lair
     * @param zone
     *                 the entity ID of the lair zone (usually itself)
     *
     * @return the array of IDs for the generated rooms
     */
    private List<Long> generateRooms(final int size, final long zone) {
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
                    grid[i][j].registerComponent(new Room(l, zone));
                    grid[i][j].registerComponent(new Descriptive(
                            "a dark cavern",
                            "This room is lit low by your lantern, but you "
                                    + "cannot see very far."));
                    store.commit(grid[i][j]);
                }
            }
        }

        return Arrays.stream(grid).flatMap(a -> Arrays.stream(a))
                .filter(Objects::nonNull).map(Entity::getId)
                .collect(Collectors.toList());
    }

    /**
     * Helper method for generating lair wumpus.
     *
     * @param rooms
     *                      lair rooms
     * @param firstRoom
     *                      starting room to not put wumpus in
     * @param zone
     *                      lair zone ID
     * @return ID of new wumpus entity
     */
    private long generateWumpus(final List<Long> rooms, final long firstRoom,
            final long zone) {
        if (rooms.size() > 1) {
            long wumpusRoom;
            do {
                wumpusRoom = rooms.get(random.get() % rooms.size());
            } while (wumpusRoom == firstRoom);
            final Entity we = store.create();
            we.registerComponent(new Wumpus());
            we.registerComponent(new Physical(wumpusRoom, zone));
            we.registerComponent(new Transit(wumpusRoom));
            store.commit(we);
            return we.getId();
        } else {
            return -1L;
        }
    }

    /**
     * Helper method for generating lair super bats.
     *
     * @param rooms
     *                      lair rooms
     * @param firstRoom
     *                      starting room to not put bats in
     * @param zone
     *                      lair zone ID
     * @return the set of entity IDs generated
     */
    private Set<Long> generateBats(final List<Long> rooms, final long firstRoom,
            final long zone) {
        Set<Long> bats = new HashSet<>();
        for (int i = 0; i <= rooms.size() / BAT_FACTOR; i++) {
            long batRoom;
            do {
                batRoom = rooms.get(random.get() % rooms.size());
            } while (batRoom == firstRoom);

            final Entity bat = store.create();
            bat.registerComponent(new SuperBat());
            bat.registerComponent(new Physical(batRoom, zone));
            bat.registerComponent(new Transit(batRoom));
            store.commit(bat);
            bats.add(bat.getId());
        }
        return bats;
    }

    /**
     * Helper method for generating pit traps.
     *
     * @param rooms
     *                      lair rooms
     * @param firstRoom
     *                      starting room to not put pit traps in
     * @param zone
     *                      lair zone ID
     * @return the set of entity IDs generated
     */
    private Set<Long> generatePits(final List<Long> rooms, final long firstRoom,
            final long zone) {
        Set<Long> pits = new HashSet<>();
        for (int i = 0; i <= rooms.size() / PIT_FACTOR; i++) {
            long pitRoom;
            do {
                pitRoom = rooms.get(random.get() % rooms.size());
            } while (pitRoom == firstRoom);

            final Entity pit = store.create();
            pit.registerComponent(new PitTrap());
            pit.registerComponent(new Hidden());
            pit.registerComponent(new Physical(pitRoom, zone));
            pit.registerComponent(new Transit(pitRoom));
            store.commit(pit);
            pits.add(pit.getId());
        }
        return pits;
    }

    /**
     * Create a new layer with a given number of rooms.
     *
     * @param size
     *                 the number of rooms to be generated in the lair.
     * @return the ID of the generated lair.
     */
    private long createLair(final int size) {
        final Entity lair = store.create();
        final List<Long> rooms = generateRooms(size, lair.getId());
        final long firstRoom = rooms.get(random.get() % rooms.size());
        final long wumpus = generateWumpus(rooms, firstRoom, lair.getId());
        final Set<Long> bats = generateBats(rooms, firstRoom, lair.getId());
        final Set<Long> pits = generatePits(rooms, firstRoom, lair.getId());
        final Set<Long> contents = new HashSet<>();
        contents.add(wumpus);
        contents.addAll(rooms);
        contents.addAll(bats);
        contents.addAll(pits);

        final Entity entrance = store.create();
        entrance.registerComponent(
                new Room(Map.of(Direction.down, firstRoom), lair.getId()));
        entrance.registerComponent(new Descriptive(
                "the mouth of a cave opening",
                "In the forest floor there is a big hole, with the stench of a"
                        + " wumpus rising from it. "));
        final Room firstRoomE = store.get(firstRoom).get()
                .getComponent(Room.class);
        firstRoomE.registerComponent(
                new Room(firstRoomE, Direction.up, entrance.getId()));
        lair.registerComponent(new Lair(entrance.getId(), wumpus));
        lair.registerComponent(new Container(
                contents.stream().mapToLong(l -> l.longValue()).toArray()));
        store.commit(lair);
        store.commit(entrance);
        store.commit(firstRoomE.getEntity());
        return lair.getId();
    }

    @Override
    public void tick() {
        final Optional<Lair> defaultLair = store.stream().component(Lair.class)
                .findAny();
        if (defaultLair.isEmpty()) {
            LOG.info("No lair found.  Creating a new default.");
            createLair(defaultSize);
        }

        final StringBuilder exs = new StringBuilder();
        store.stream().components(Set.of(Room.class, ArrowHit.class))
                .forEach(cm -> {
                    exs.append(ARROW_MISS);
                    store.stream().component(Wumpus.class)
                            .filter(w -> !w.hasComponent(Dead.class))
                            .forEach(w -> {
                                final long wLoc = w.getComponent(Physical.class)
                                        .getLocation();
                                final List<Long> ds = store.get(wLoc).get()
                                        .getComponent(Room.class)
                                        .getLinkedRooms().values().stream()
                                        .collect(Collectors.toList());
                                final long wDest = ds
                                        .get(random.get() % ds.size());
                                w.registerComponent(new Transit(wDest));
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
