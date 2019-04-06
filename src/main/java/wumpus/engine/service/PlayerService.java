package wumpus.engine.service;

import java.util.Optional;
import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Arrow;
import wumpus.engine.entity.component.Component;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Inventory;
import wumpus.engine.entity.component.Item;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Tavern;
import wumpus.engine.entity.component.Transit;

/**
 * Service for the managment of players.
 */
public final class PlayerService implements Service {

    /**
     * Execution priority of this service.
     */
    public static final int PRIORITY = 5;

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * Creates a new player service with the given entity store.
     *
     * @param s
     *              the entity store to be used by this service
     */
    public PlayerService(final EntityStore s) {
        this.store = s;
    }

    /**
     * Create a new player entity.
     *
     * @param e
     *              the entity to associate the player component with
     */
    private void createPlayer(final Entity e) {
        final Entity a = store.create();
        a.registerComponent(new Arrow());
        store.commit(a);
        final Entity i = store.create();
        i.registerComponent(new Inventory());
        i.registerComponent(new Container(a.getId()));
        store.commit(i);
        e.registerComponent(new Player(i.getId()));
        store.commit(e);
    }

    /**
     * Get an item entity from a player's inventory of a particular type.
     *
     * @param playerId
     *                     the entity ID of the player to get an item from
     * @param itemType
     *                     the component class of the item to get
     * @return that item entity if present
     */
    public Optional<Entity> getInventoryItem(final long playerId,
            final Class<? extends Component> itemType) {
        final Entity player = store.get(playerId).get();
        final Entity inventory = store
                .get(player.getComponent(Player.class).getInventory()).get();
        return inventory.getComponent(Container.class).getContents().stream()
                .map(c -> store.get(c).get())
                .filter(e -> e.hasComponent(itemType)).findFirst();
    }

    /**
     * Adjust the amount of an item in a player's inventory. Will remove the
     * item if it drops the count to 0 or less.
     *
     * @param playerId
     *                       ID of the player to adjust
     * @param itemType
     *                       type of item to adjust
     * @param adjustment
     *                       amount to adjust by
     */
    public void adjustInventoryItem(final long playerId,
            final Class<? extends Component> itemType, final int adjustment) {
        final Entity player = store.get(playerId).get();
        final Container container = store
                .get(player.getComponent(Player.class).getInventory()).get()
                .getComponent(Container.class);
        final Entity item = container.getContents().stream()
                .map(c -> store.get(c).get())
                .filter(e -> e.hasComponent(itemType)).findFirst().get();
        final Item i = item.getComponent(Item.class);
        if (i.getCount() + adjustment <= 0) {
            container.registerComponent(
                    new Container(container, c -> c != item.getId()));
            store.commit(container.getEntity().get());
        } else {
            item.registerComponent(new Item(i, adjustment));
            store.commit(item);
        }
    }

    @Override
    public void tick() {
        // Find listeners without players and attach
        store.stream().component(Listener.class)
                .filter(c -> !c.hasComponent(Player.class))
                .forEach(l -> createPlayer(l.getEntity().get()));

        final Optional<Tavern> start = store.stream().component(Tavern.class)
                .findAny();

        if (start.isPresent()) {
            store.stream().components(Set.of(Player.class, Physical.class))
                    .map(m -> m.getByComponent(Physical.class))
                    .filter(p -> p.getLocation().isEmpty())
                    .map(p -> p.getEntity().get())
                    .forEach(e -> e.registerComponent(new Transit(
                            start.get().getEntity().get().getId())));
        }
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
