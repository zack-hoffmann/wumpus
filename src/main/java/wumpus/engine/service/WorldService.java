package wumpus.engine.service;

import java.util.Map;
import java.util.Optional;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Descriptive;
import wumpus.engine.entity.component.Lair;
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
    public static final int PRIORITY = 79;

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
     * Create a tavern room.
     *
     * @return new tavern entity
     */
    private Entity createTavern() {
        final Entity e = store.create();
        e.registerComponent(new Tavern());
        e.registerComponent(new Container());
        e.registerComponent(new Room());
        e.registerComponent(
                new Descriptive("The 'Booth and Ale' Tavern", TAVERN_DESC));
        store.commit(e);
        return e;
    }

    /**
     * Create a wilderness room.
     *
     * @return new wilderness entity
     */
    private Entity createWilderness() {
        final Entity e = store.create();
        e.registerComponent(new Wilderness());
        e.registerComponent(new Container());
        e.registerComponent(new Room());
        e.registerComponent(new Descriptive("The Wilderness", WILDERNESS_DESC));
        store.commit(e);
        return e;
    }

    @Override
    public void tick() {
        final Entity tavern = store.stream().component(Tavern.class)
                .map(t -> t.getEntity().get()).findAny()
                .orElseGet(() -> this.createTavern());
        final Entity wilderness = store.stream().component(Wilderness.class)
                .map(t -> t.getEntity().get()).findAny()
                .orElseGet(() -> this.createWilderness());

        tavern.registerComponent(
                new Room(Map.of(Direction.north, wilderness.getId())));
        // TODO needs random
        final Optional<Entity> randomLair = store.stream().component(Lair.class)
                .map(l -> l.getEntity().get()).findAny();
        if (randomLair.isPresent()) {
            wilderness.registerComponent(new Room(Map.of(Direction.south,
                    tavern.getId(), Direction.north,
                    randomLair.get().getComponent(Lair.class).getEntrace())));
        }

        store.commit(tavern);
        store.commit(wilderness);
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
