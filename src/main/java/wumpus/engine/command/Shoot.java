package wumpus.engine.command;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Stream;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Arrow;
import wumpus.engine.entity.component.ArrowHit;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Hidden;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.PitTrap;
import wumpus.engine.entity.component.Room;
import wumpus.engine.service.PlayerService;
import wumpus.engine.type.Direction;

/**
 * Command to shoot the crooked arrow.
 */
public final class Shoot implements Command {

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        final PlayerService playerService = new PlayerService(store);
        final Entity se = store.get(source).get();
        final Optional<Entity> arrows = playerService.getInventoryItem(source,
                Arrow.class);
        final OptionalLong location = se.getComponent(Physical.class)
                .getLocation();
        final Optional<Direction> direction = Direction.match(args[0]);
        if (se.hasComponent(Dead.class)) {
            return "You cannot shoot, for you are dead.";
        } else if (arrows.isEmpty()) {
            return "You do not have any arrows.";
        } else if (location.isEmpty()) {
            return "You cannot shoot from where you are...";
        } else if (args.length < 1) {
            return "You must provide a direction to shoot.";
        } else if (direction.isEmpty()) {
            return "Not a valid direction.";
        } else {
            final Optional<Entity> dest = store.get(store
                    .get(location.getAsLong()).get().getComponent(Room.class)
                    .getLinkedRooms().get(direction.get()));
            if (dest.isEmpty()) {
                return "You cannot shoot in that direction.";
            } else {
                playerService.adjustInventoryItem(source, Arrow.class, -1);
                final Stream<Entity> contents = dest.get()
                        .getComponent(Container.class).getContents().stream()
                        .map(l -> store.get(l).get());
                final Optional<Entity> target = contents
                        .filter(e -> e.hasComponent(Physical.class)
                                && !e.hasComponent(Dead.class)
                                && (e.hasComponent(Hidden.class)
                                        || !e.hasComponent(PitTrap.class)))
                        .findAny();
                if (target.isPresent()) {
                    target.get().registerComponent(new ArrowHit());
                    store.commit(target.get());
                } else {
                    dest.get().registerComponent(new ArrowHit());
                    store.commit(dest.get());
                }
                return "You shoot your crooked arrow " + direction.get().name()
                        + "ward.";
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