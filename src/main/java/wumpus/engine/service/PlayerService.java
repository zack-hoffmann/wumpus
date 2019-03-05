package wumpus.engine.service;

import java.util.Optional;
import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Arrow;
import wumpus.engine.entity.component.Component;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Descriptive;
import wumpus.engine.entity.component.Item;
import wumpus.engine.entity.component.Lair;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Transit;
import wumpus.io.IOAdapter;

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
     * @param arrowCount
     *                       starting arrow count
     * @return the id of the new player entity
     */
    public long createPlayer(final int arrowCount) {
        final Entity a = store.create();
        a.registerComponent(new Arrow());
        a.registerComponent(new Descriptive("arrow"));
        a.registerComponent(new Item(arrowCount));
        store.commit(a);
        final Entity i = store.create();
        i.registerComponent(new Descriptive("the contents of your inventory"));
        i.registerComponent(new Container(a.getId()));
        store.commit(i);
        final Entity e = store.create();
        e.registerComponent(new Player(i.getId()));
        e.registerComponent(new Physical());
        store.commit(e);
        return e.getId();
    }

    /**
     * Associate a player with an I/O-based listener.
     *
     * @param playerId
     *                     entity ID of the player
     * @param io
     *                     I/O adapter for the player
     */
    public void attachPlayer(final long playerId, final IOAdapter io) {
        store.get(playerId).ifPresent(e -> {
            e.registerComponent(new Listener(m -> {
                io.post("\n" + m.toString());
            }));
            store.commit(e);
        });
    }

    /**
     * Disassociate a player from their listener.
     *
     * @param playerId
     *                     entity ID of the player
     */
    public void detachPlayer(final long playerId) {
        store.get(playerId).ifPresent(e -> {
            e.deregisterComponent(Listener.class);
            store.commit(e);
        });
    }

    /**
     * Count the number of active players in the game. This goes by players with
     * listeners attached.
     *
     * @return the number of players active in the game
     */
    public long getAttachedPlayerCount() {
        return store.stream().components(Set.of(Player.class, Listener.class))
                .count();
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
        final Optional<Lair> defaultLair = store.stream().component(Lair.class)
                .findAny();

        // If a default lair is available, move all players with no location to
        // that lair entrance.
        if (defaultLair.isPresent()) {
            store.stream().components(Set.of(Player.class, Physical.class))
                    .map(m -> m.getByComponent(Physical.class))
                    .filter(p -> p.getLocation().isEmpty())
                    .map(p -> p.getEntity().get())
                    .forEach(e -> e.registerComponent(
                            new Transit(defaultLair.get().getEntrace())));
        }
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
