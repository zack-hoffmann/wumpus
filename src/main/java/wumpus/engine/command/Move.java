package wumpus.engine.command;

import java.util.Optional;
import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.Transit;
import wumpus.engine.type.Direction;

/**
 * Moves an entity by applying a transit component.
 */
public final class Move implements Command {

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        final Entity se = store.get(source).get();
        final long location = se.component(Physical.class).location();
        final Optional<Direction> direction = Direction.match(args[0]);
        if (se.hasComponent(Dead.class)) {
            return "You cannot move, for you are dead.";
        } else if (args.length < 1) {
            return "You must provide a direction.";
        } else if (direction.isEmpty()) {
            return "Not a valid direction.";
        } else {
            final Long dest = store.get(location).get().component(Room.class)
                    .linkedRooms().get(direction.get());
            if (dest == null) {
                return "You cannot move in that direction.";
            } else {

                store.get(source).get().registerComponent(new Transit(dest));
                return "You head " + direction.get().name() + "...";
            }
        }
    }

    @Override
    public String name() {
        return "move";
    }

    @Override
    public Set<String> aliases() {
        return Set.of();
    }

}
