package wumpus.engine.command;

import java.util.OptionalLong;
import java.util.Set;

import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.Transit;

/**
 * Moves an entity by applying a transit component.
 */
public final class Move implements Command {

    /**
     * Set of valid movement directions.
     */
    public static final Set<String> DIRECTIONS = Set.of("north", "south",
            "east", "west", "up", "down");

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        final OptionalLong location = store.get(source).get()
                .getComponent(Physical.class).getLocation();
        if (location.isEmpty()) {
            return "You cannot move from where you are...";
        } else if (args.length < 1) {
            return "You must provide a direction.";
        } else if (!DIRECTIONS.contains(args[0])) {
            return "Not a valid direction.";
        } else {
            final Long dest = store.get(location.getAsLong()).get()
                    .getComponent(Room.class).getLinkedRooms().get(args[0]);
            if (dest == null) {
                return "You cannot move in that direction.";
            } else {

                store.get(source).get().registerComponent(
                        new Transit(location.getAsLong(), dest));
                return "You head " + args[0] + "...";
            }
        }
    }

    @Override
    public String getName() {
        return "move";
    }

    @Override
    public Set<String> getAliases() {
        return Set.of();
    }

}
