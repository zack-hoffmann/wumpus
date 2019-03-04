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
            targetContents
                    .addAll(target.getComponent(Container.class).getContents());
        }

        if (target.hasComponent(Room.class)) {
            exits.putAll(target.getComponent(Room.class).getLinkedRooms());
        }

        out.append("\nO--- You ");
        if (targetContents.contains(p.getEntity().get().getId())) {
            out.append("are in");
        } else {
            out.append("see");
        }

        out.append(" " + target.getShortDescription() + ". ---O\n");

        targetContents.stream().map(id -> store.get(id).get())
                .collect(EntityStream.collector()).component(Descriptive.class)
                .filter(d -> !d.hasComponent(Hidden.class)).map(d -> {
                    if (d.getEntity().get().hasComponent(Dead.class)) {
                        return "The corpse of " + d.getShortDescription();
                    } else {
                        return d.getShortDescription();
                    }
                }).map(d -> TextTools.capitalize(d))
                .forEach(d -> out.append("   " + d + " is here.\n"));

        if (!exits.isEmpty()) {
            out.append("You see the following exits:\n");
            out.append(exits.entrySet().stream()
                    .map(e -> "   " + TextTools.capitalize(e.getKey().name())
                            + " - "
                            + store.get(e.getValue()).get()
                                    .getComponent(Descriptive.class)
                                    .getShortDescription())
                    .collect(Collectors.joining("\n")));
            out.append("\n");
            final Set<Entity> contents = exits.entrySet().stream()
                    .flatMap(e -> store.get(e.getValue()).get()
                            .getComponent(Container.class).getContents()
                            .stream())
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

        p.getComponent(Listener.class).tell(out.toString());
    }

    @Override
    public void tick() {
        store.stream()
                .components(
                        Set.of(Player.class, Listener.class, Examining.class))
                .forEach(cm -> {
                    final Player p = cm.getByComponent(Player.class);
                    final Entity t = store
                            .get(cm.getByComponent(Examining.class).getTarget())
                            .get();
                    if (t.hasComponent(Descriptive.class)) {
                        examine(p, t.getComponent(Descriptive.class));
                    }
                    p.deregisterComponent(Examining.class);
                    store.commit(p.getEntity().get());
                });
    }

}
