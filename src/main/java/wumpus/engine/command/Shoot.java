package wumpus.engine.command;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Stream;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.Wumpus;

/**
 * Command to shoot the crooked arrow.
 */
public final class Shoot implements Command {

    /**
     * Set of valid movement directions.
     */
    public static final Set<String> DIRECTIONS = Set.of("north", "south",
            "east", "west", "up", "down");

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        final Entity se = store.get(source).get();
        final OptionalLong location = se.getComponent(Physical.class)
                .getLocation();
        final String direction = DIRECTIONS.stream()
                .filter(s -> args.length > 0 && s.startsWith(args[0])).findAny()
                .orElseGet(() -> "inv");
        if (se.hasComponent(Dead.class)) {
            return "You cannot shoot, for you are dead.";
        } else if (location.isEmpty()) {
            return "You cannot shoot from where you are...";
        } else if (args.length < 1) {
            return "You must provide a direction to shoot.";
        } else if (!DIRECTIONS.contains(direction)) {
            return "Not a valid direction.";
        } else {
            final Long dest = store.get(location.getAsLong()).get()
                    .getComponent(Room.class).getLinkedRooms().get(direction);
            if (dest == null) {
                return "You cannot move in that direction.";
            } else {
                final Stream<Entity> contents = store.get(dest).get()
                        .getComponent(Container.class).getContents().stream()
                        .map(l -> store.get(l).get());
                final Optional<Entity> wumpus = contents
                        .filter(e -> e.hasComponent(Wumpus.class)
                                && !e.hasComponent(Dead.class))
                        .findAny();
                if (wumpus.isPresent()) {
                    wumpus.get().registerComponent(new Dead());
                    store.commit(wumpus.get());
                    return "You loose your crooked arrow " + direction
                            + "ward and hear a satisfying 'thunk' as it "
                            + "makes contact.  You then hear a beastial "
                            + "groan and a thud as your prey collapses!";
                } else {
                    return "You loose your crooked arrow " + direction
                            + "ward and hear a disappointing 'clank' as it "
                            + "falls to the ground.";
                }
            }
        }
    }

    @Override
    public String getName() {
        return "shoot";
    }

    @Override
    public Set<String> getAliases() {
        return Set.of("sh");
    }

}
