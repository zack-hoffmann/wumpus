package wumpus.engine.command;

import java.util.Optional;
import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Arrow;
import wumpus.engine.entity.component.ArrowHit;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Hidden;
import wumpus.engine.entity.component.Item;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.PitTrap;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Room;
import wumpus.engine.type.Direction;

/**
 * Command to shoot the crooked arrow.
 */
public final class Shoot implements Command {

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        final Entity se = store.get(source).get();
        final Entity inventory = store
                .get(se.component(Player.class).inventory()).get();
        final Optional<Entity> arrows = inventory.contentsStream(store)
                .filter(e -> e.hasComponent(Arrow.class)).findFirst();
        final long location = se.component(Physical.class).location();
        final Optional<Direction> direction = Direction.match(args[0]);
        if (se.hasComponent(Dead.class)) {
            return "You cannot shoot, for you are dead.";
        } else if (arrows.isEmpty()) {
            return "You do not have any arrows.";
        } else if (args.length < 1) {
            return "You must provide a direction to shoot.";
        } else if (direction.isEmpty()) {
            return "Not a valid direction.";
        } else {
            // TODO shooting in an invalid direction not getting picked up (NPE)
            final Optional<Entity> dest = store.get(store.get(location).get()
                    .component(Room.class).linkedRooms().get(direction.get()));
            if (dest.isEmpty()) {
                return "You cannot shoot in that direction.";
            } else {
                final Entity container = store
                        .get(se.component(Player.class).inventory()).get();
                final Entity item = container.contentsStream(store)
                        .filter(e -> e.hasComponent(Arrow.class)).findFirst()
                        .get();
                final Item i = item.component(Item.class);
                if (i.count() - 1 <= 0) {
                    container.registerComponent(
                            new Container(container.component(Container.class),
                                    c -> c != item.id()));
                    store.commit(container);
                } else {
                    item.registerComponent(new Item(i, -1));
                    store.commit(item);
                }
                final Optional<Entity> target = dest.get().contentsStream(store)
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
    public String name() {
        return "shoot";
    }

    @Override
    public Set<String> aliases() {
        return Set.of("sh");
    }

}
