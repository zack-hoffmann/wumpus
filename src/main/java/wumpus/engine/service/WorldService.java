package wumpus.engine.service;

import java.util.Map;
import java.util.Optional;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Descriptive;
import wumpus.engine.entity.component.Lair;
import wumpus.engine.entity.component.Overworld;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.Tavern;
import wumpus.engine.entity.component.Wilderness;
import wumpus.engine.type.Direction;

/**
 * Service for managing the state of the world exterior to the wumpus lairs.
 */
public final class WorldService implements Service {

    /**
     * Execution priority of this service.
     */
    private static final int PRIORITY = 79;

    /**
     * Description of the tavern.
     */
    private static final String TAVERN_DESC = ""
            + "This quaint woodland shack is the northernmost stronghold of "
            + "humanity against the harsh northern wilderness. All manner of "
            + "hunter, explorer, and thrill-seeker gather here for a humble "
            + "send-off, and sometimes for a celebratory return. It consists "
            + "of a just a small bar room with a handful of tables, a untuned "
            + "piano, an unkempt barman, and some sparse, dusty lanterns.";

    /**
     * Description of the wilderness.
     */
    private static final String WILDERNESS_DESC = ""
            + "This is the deep, dark, woodland in the far reaches of the "
            + "world, untouched by civilized man.  Beasts, savages, and all "
            + "manner of other hazards adorn this land.";

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * Create a new service with a given entity store.
     *
     * @param s
     *              the entity store for this service
     */
    public WorldService(final EntityStore s) {
        this.store = s;
    }

    /**
     * Create the overworld zone.
     *
     * @return new overworld zone
     */
    private Entity createOverworld() {
        final Entity e = store.create();
        e.registerComponent(new Overworld());
        store.commit(e);
        return e;
    }

    /**
     * Create a tavern room.
     *
     * @param zone
     *                 the entity ID of the overworld zone
     * @return new tavern entity
     */
    private Entity createTavern(final long zone) {
        final Entity e = store.create();
        e.registerComponent(new Tavern());
        e.registerComponent(new Room(zone));
        e.registerComponent(
                new Descriptive("The 'Booth and Ale' Tavern", TAVERN_DESC));
        store.commit(e);
        return e;
    }

    /**
     * Create a wilderness room.
     *
     * @param zone
     *                 the entity ID of the overworld zone
     * @return new wilderness entity
     */
    private Entity createWilderness(final long zone) {
        final Entity e = store.create();
        e.registerComponent(new Wilderness());
        e.registerComponent(new Room(zone));
        e.registerComponent(new Descriptive("the wilderness", WILDERNESS_DESC));
        store.commit(e);
        return e;
    }

    @Override
    public void tick() {
        final Entity overworld = store.stream().component(Overworld.class)
                .map(t -> t.entity()).findAny()
                .orElseGet(() -> this.createOverworld());
        final Entity tavern = store.stream().component(Tavern.class)
                .map(t -> t.entity()).findAny()
                .orElseGet(() -> this.createTavern(overworld.id()));
        final Entity wilderness = store.stream().component(Wilderness.class)
                .map(t -> t.entity()).findAny()
                .orElseGet(() -> this.createWilderness(overworld.id()));

        tavern.registerComponent(
                new Room(Map.of(Direction.north, wilderness.id()),
                        overworld.id()));
        // TODO needs random
        final Optional<Entity> randomLair = store.stream().component(Lair.class)
                .map(l -> l.entity()).findAny();
        if (randomLair.isPresent()) {
            final Lair lair = randomLair.get().component(Lair.class);
            final Room entrace = store.get(lair.entrance()).get()
                    .component(Room.class);
            lair.registerComponent(
                    new Room(entrace, Direction.south, wilderness.id()));
            store.commit(lair.entity());
            wilderness
                    .registerComponent(new Room(
                            Map.of(Direction.south, tavern.id(),
                                    Direction.north, lair.entrance()),
                            overworld.id()));
        }

        store.commit(tavern);
        store.commit(wilderness);
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
