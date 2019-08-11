package wumpus.engine.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.ArrowHit;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Hazard;
import wumpus.engine.entity.component.Hidden;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.PitTrap;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.SuperBat;
import wumpus.engine.entity.component.Transit;
import wumpus.engine.entity.component.Wumpus;

/**
 * Service to manage all of the game world hazards.
 */
public final class HazardService implements Service {

    /**
     * Execution priority of this service.
     */
    private static final int PRIORITY = 100;

    /**
     * Message displayed to a player when a wumpus kills them.
     */
    private static final String DEATH = "The wumpus snaps you up in its "
            + "mighty jaws and quickly puts an end to your life!\n";

    /**
     * Sound of a wumpus dying.
     */
    private static final String WUMPUS_DEATH = "You hear a satisfying 'thunk' "
            + "as an arrow makes contact with flesh, followed by a beastial "
            + "groan and a thud as a wumpus collapses!\n";

    /**
     * Description of a super bat moving a player.
     */
    private static final String BAT_MOVE = "A super bat swoops down and scoops "
            + "you up, carrying to another location.\n";

    /**
     * The sound of a super bat dying.
     */
    private static final String BAT_DEATH = "You hear the fatal shriek of a bat"
            + " dying.\n";

    /**
     * The sound of a pit trap crumbling open.
     */
    private static final String PIT_OPEN = "You hear a clang, followed by the "
            + "low rumble of stone crubmling and falling away.\n";

    /**
     * Description of a player falling in to a pit.
     */
    private static final String PIT_DEATH = "The ground gives way beneath you "
            + "and you plummet several hundred feet to your death.\n";

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * Create a hazard service for the given entity store.
     *
     * @param s
     *              the entity store used
     */
    public HazardService(final EntityStore s) {
        this.store = s;
    }

    /**
     * Action of a player encountering a super bat.
     *
     * @param player
     *                   the player component
     * @param bat
     *                   the bat component
     */
    private void batMove(final Player player, final SuperBat bat) {
        if (!bat.hasComponent(Dead.class)) {
            final List<Room> rooms = store.stream().component(Room.class)
                    .collect(Collectors.toList());
            final long playerRoom = rooms
                    .get(new Random().nextInt(rooms.size())).entity().id();
            final long batRoom = rooms.get(new Random().nextInt(rooms.size()))
                    .entity().id();
            player.registerComponent(new Transit(playerRoom));
            bat.registerComponent(new Transit(batRoom));
            store.commit(player.entity());
            store.commit(bat.entity());
            if (player.hasComponent(Listener.class)) {
                player.component(Listener.class).tell(BAT_MOVE);
            }
        }
    }

    /**
     * Action of a player encountering a pit trap.
     *
     * @param player
     *                   the player component
     * @param pit
     *                   the pit component
     */
    private void pitTrigger(final Player player, final PitTrap pit) {
        if (pit.hasComponent(Hidden.class)) {
            pit.deregisterComponent(Hidden.class);
            player.registerComponent(new Dead());
            store.commit(pit.entity());
            store.commit(pit.entity());
            if (player.hasComponent(Listener.class)) {
                player.component(Listener.class).tell(PIT_DEATH);
            }
        }
    }

    /**
     * Action of a player being killed by a wumpus.
     *
     * @param player
     *                   the player being killed
     * @param wumpus
     *                   the wumpus killing the player
     */
    private void wumpusKill(final Player player, final Wumpus wumpus) {
        if (!wumpus.hasComponent(Dead.class)) {
            player.registerComponent(new Dead());
            store.commit(player.entity());
            player.component(Listener.class).tell(DEATH);
        }
    }

    @Override
    public void tick() {
        store.stream().components(Set.of(Hazard.class, Physical.class))
                .map(cm -> cm.entity()).forEach(e -> {
                    final long loc = e.component(Physical.class).location();
                    final Optional<Player> player = store.get(loc).get()
                            .contentsStream(store).component(Player.class)
                            .filter(p -> !p.hasComponent(Dead.class))
                            .findFirst();
                    if (player.isPresent()) {
                        if (e.hasComponent(SuperBat.class)) {
                            batMove(player.get(), e.component(SuperBat.class));
                        } else if (e.hasComponent(PitTrap.class)) {
                            pitTrigger(player.get(),
                                    e.component(PitTrap.class));
                        } else if (e.hasComponent(Wumpus.class)) {
                            wumpusKill(player.get(), e.component(Wumpus.class));
                        }
                    }

                });
        final StringBuilder exs = new StringBuilder();
        store.stream().components(Set.of(Hazard.class, ArrowHit.class))
                .map(cm -> cm.entity()).forEach(e -> {
                    if (e.hasComponent(SuperBat.class)) {
                        e.registerComponent(new Dead());
                        exs.append(BAT_DEATH);
                    } else if (e.hasComponent(PitTrap.class)
                            && e.hasComponent(Hidden.class)) {
                        e.deregisterComponent(Hidden.class);
                        exs.append(PIT_OPEN);
                    } else if (e.hasComponent(Wumpus.class)) {
                        e.registerComponent(new Dead());
                        exs.append(WUMPUS_DEATH);
                    }
                    e.deregisterComponent(ArrowHit.class);
                    store.commit(e);
                });
        if (exs.length() > 0) {
            store.stream().components(Set.of(Player.class, Listener.class))
                    .map(cm -> cm.byComponent(Listener.class))
                    .forEach(l -> l.tell(exs.toString()));
        }
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
