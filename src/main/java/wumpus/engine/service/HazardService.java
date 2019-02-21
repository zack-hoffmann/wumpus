package wumpus.engine.service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.EntityStream;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Dead;
import wumpus.engine.entity.component.Descriptive;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Room;
import wumpus.engine.entity.component.SuperBat;
import wumpus.engine.entity.component.Transit;

/**
 * Service to manage all of the game world hazards.
 */
public final class HazardService implements Service {

    /**
     * Description of a super bat moving a player.
     */
    private static final String BAT_MOVE = "A super bat swoops down and scoops "
            + "you up, carrying to another location.\n";

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
     * Add a Super Bat at the given room entity.
     *
     * @param location
     *                     entity ID of a room to put the bat in
     * @return the entity ID of the new bat
     */
    public long createBat(final long location) {
        final Entity bat = store.create();
        bat.registerComponent(new SuperBat());
        bat.registerComponent(new Physical());
        bat.registerComponent(new Transit(location));
        bat.registerComponent(new Descriptive("a super bat",
                "a massive bat with an ear-piercing screech"));
        store.commit(bat);
        return bat.getId();
    }

    @Override
    public void tick() {
        store.stream().components(Set.of(SuperBat.class, Physical.class))
                .forEach(cm -> {
                    final OptionalLong loc = cm.getByComponent(Physical.class)
                            .getLocation();
                    if (loc.isPresent()) {
                        final Optional<Player> player = store
                                .get(loc.getAsLong()).get()
                                .getComponent(Container.class).getContents()
                                .stream().map(i -> store.get(i).get())
                                .collect(EntityStream.collector())
                                .component(Player.class)
                                .filter(p -> !p.hasComponent(Dead.class))
                                .findFirst();
                        if (player.isPresent()) {
                            final List<Room> rooms = store.stream()
                                    .component(Room.class)
                                    .collect(Collectors.toList());
                            final long playerRoom = rooms
                                    .get(new Random().nextInt(rooms.size()))
                                    .getEntity().get().getId();
                            final long batRoom = rooms
                                    .get(new Random().nextInt(rooms.size()))
                                    .getEntity().get().getId();
                            player.get().registerComponent(
                                    new Transit(loc.getAsLong(), playerRoom));
                            cm.getByComponent(SuperBat.class).registerComponent(
                                    new Transit(loc.getAsLong(), batRoom));
                            store.commit(player.get().getEntity().get());
                            store.commit(cm.getByComponent(SuperBat.class)
                                    .getEntity().get());
                            if (player.get().hasComponent(Listener.class)) {
                                player.get().getComponent(Listener.class)
                                        .tell(BAT_MOVE);
                            }
                        }
                    }
                });
    }

}
