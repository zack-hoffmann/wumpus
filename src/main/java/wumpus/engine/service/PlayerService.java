package wumpus.engine.service;

import java.util.Optional;
import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Arrow;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Cooldown;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Descriptive;
import wumpus.engine.entity.component.Inventory;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Tavern;
import wumpus.engine.entity.component.Transit;
import wumpus.engine.entity.component.Void;

/**
 * Service for the managment of players.
 */
public final class PlayerService implements Service {

    /**
     * Execution priority of this service.
     */
    private static final int PRIORITY = 5;

    /**
     * Message to display on player respawn.
     */
    private static final String RESPAWN = "Your soul is whisked away in to the "
            + "body of a new adventurer!";

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
        final long voidId = store.stream().component(Void.class)
                .map(v -> v.getEntity().get()).findAny().get().getId();
        e.registerComponent(new Player(newPlayerInventory()));
        e.registerComponent(new Physical(voidId, voidId));
        e.registerComponent(new Transit(voidId));
        store.commit(e);
    }

    /**
     * Generate the inventory of a new player.
     *
     * @return the entity ID of the new inventory
     */
    private long newPlayerInventory() {
        final Entity a = store.create();
        a.registerComponent(new Arrow());
        store.commit(a);
        final Entity i = store.create();
        i.registerComponent(new Inventory());
        i.registerComponent(new Container(a.getId()));
        store.commit(i);
        return i.getId();
    }

    /**
     * Generate a void space.
     *
     * @return the entity of the new void
     */
    private Entity createVoid() {
        final Entity e = store.create();
        e.registerComponent(new Void());
        e.registerComponent(new Descriptive("an infinite abyss", "nothing"));
        e.registerComponent(new Container(e.getId()));
        store.commit(e);
        return e;
    }

    @Override
    public void tick() {
        if (!store.stream().component(Void.class).findAny().isPresent()) {
            createVoid();
        }
        // Find listeners without players and attach
        store.stream().component(Listener.class)
                .filter(c -> !c.hasComponent(Player.class))
                .forEach(l -> createPlayer(l.getEntity().get()));

        final Optional<Tavern> start = store.stream().component(Tavern.class)
                .findAny();
        final Optional<Void> voidz = store.stream().component(Void.class)
                .findAny();

        if (start.isPresent() && voidz.isPresent()) {
            final Entity voide = voidz.get().getEntity().get();
            voide.getComponent(Container.class).getContents().stream()
                    .map(l -> store.get(l).get())
                    .filter(e -> e.hasComponent(Player.class))
                    .forEach(e -> e.registerComponent(new Transit(
                            start.get().getEntity().get().getId())));
            store.stream().components(Set.of(Player.class, Dead.class))
                    .map(cm -> cm.getEntity())
                    .filter(e -> !e.hasComponent(Cooldown.class)).forEach(e -> {
                        e.deregisterComponent(Dead.class);
                        e.registerComponent(new Player(newPlayerInventory()));
                        e.registerComponent(new Transit(
                                start.get().getEntity().get().getId()));
                        e.getComponent(Listener.class).tell(RESPAWN);
                    });
        }
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
