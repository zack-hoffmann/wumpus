package wumpus.engine.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.EntityStream;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Descriptive;
import wumpus.engine.entity.component.Examining;
import wumpus.engine.entity.component.Hidden;
import wumpus.engine.entity.component.Item;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.PitTrap;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.SuperBat;
import wumpus.engine.entity.component.Wumpus;
import wumpus.engine.type.Direction;
import wumpus.io.TextTools;

/**
 * A service for displaying descriptions to examining players.
 */
public final class ExaminingService implements Service {

    /**
     * Execution priority of this service.
     */
    private static final int PRIORITY = 20;

    /**
     * The feel of a drafty pit.
     */
    private static final String PIT_DRAFT = "You feel a cool draft.\n";

    /**
     * The sound of a super bat.
     */
    private static final String BAT_SOUND = "You hear screeching nearby.\n";

    /**
     * The smell of a wumpus in the adjacent room.
     */
    private static final String WUMPUS_SMELL = "You smell a wumpus nearby!\n";

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * Create a service for the given entity store.
     *
     * @param s
     *              the entity store used
     */
    public ExaminingService(final EntityStore s) {
        this.store = s;
    }

    /**
     * Describe a target to a player.
     *
     * @param p
     *                   the player examining
     * @param target
     *                   the entity being examined
     */
    private void examine(final Player p, final Descriptive target) {
        final StringBuilder out = new StringBuilder();
        final Set<Long> targetContents = new HashSet<>();
        final Map<Direction, Long> exits = new HashMap<>();

        if (target.hasComponent(Container.class)) {
            targetContents.addAll(target.component(Container.class).contents());
        }

        if (target.hasComponent(Room.class)) {
            exits.putAll(target.component(Room.class).linkedRooms());
        }

        out.append("\nO--- You ");
        if (targetContents.contains(p.entity().id())) {
            out.append("are in");
        } else {
            out.append("see");
        }

        out.append(" " + target.shortDescription() + ". ---O\n");

        out.append(target.longDescription() + "\n");

        targetContents.stream().map(id -> store.get(id).get())
                .collect(EntityStream.collector(store))
                .component(Descriptive.class)
                .filter(d -> !d.hasComponent(Hidden.class)).map(d -> {
                    if (d.hasComponent(Item.class)) {
                        final int count = d.component(Item.class).count();
                        if (count > 1) {
                            return count + " " + d.shortDescription() + "s are";
                        } else {
                            return count + " " + d.shortDescription();
                        }
                    } else if (d.hasComponent(Dead.class)) {
                        return "The corpse of " + d.shortDescription() + " is";
                    } else {
                        return d.shortDescription() + " is";
                    }
                }).map(d -> TextTools.capitalize(d))
                .forEach(d -> out.append("   " + d + " here.\n"));

        if (!exits.isEmpty()) {
            out.append("You see the following exits:\n");
            out.append(exits.entrySet().stream()
                    .sorted((m, n) -> m.getKey().compareTo(n.getKey()))
                    .map(e -> "   " + TextTools.capitalize(e.getKey().name())
                            + " - "
                            + store.get(e.getValue()).get()
                                    .component(Descriptive.class)
                                    .shortDescription())
                    .collect(Collectors.joining("\n")));
            out.append("\n");
            final Set<Entity> contents = exits.entrySet().stream()
                    .flatMap(e -> store.get(e.getValue()).get()
                            .component(Container.class).contents().stream())
                    .map(l -> store.get(l).get()).collect(Collectors.toSet());
            if (contents.stream().filter(e -> e.hasComponent(Wumpus.class))
                    .findAny().isPresent()) {
                out.append(WUMPUS_SMELL);
            }
            if (contents.stream()
                    .filter(e -> e.hasComponent(SuperBat.class)
                            && !e.hasComponent(Dead.class))
                    .findAny().isPresent()) {
                out.append(BAT_SOUND);
            }
            if (contents.stream().filter(e -> e.hasComponent(PitTrap.class))
                    .findAny().isPresent()) {
                out.append(PIT_DRAFT);
            }
        }

        p.component(Listener.class).tell(out.toString());
    }

    @Override
    public void tick() {
        store.stream()
                .components(
                        Set.of(Player.class, Listener.class, Examining.class))
                .forEach(cm -> {
                    final Player p = cm.byComponent(Player.class);
                    final Entity t = store
                            .get(cm.byComponent(Examining.class).target())
                            .get();
                    if (t.hasComponent(Descriptive.class)) {
                        examine(p, t.component(Descriptive.class));
                    }
                    p.deregisterComponent(Examining.class);
                    store.commit(p.entity());
                });
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

}
