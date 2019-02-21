package wumpus.engine.service;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
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
import wumpus.engine.entity.component.Wumpus;
import wumpus.engine.type.Direction;
import wumpus.io.TextTools;

/**
 * Service for moving all entities with a transit component.
 */
public final class TransitService implements Service {

    /**
     * The entity store used by this service.
     */
    private final EntityStore store;

    /**
     * The sound of a wumpus moving.
     */
    private static final String WUMPUS_MOVE = "You hear the thunderous sound "
            + "of trampling hooves as a wumpus changes its location.";

    /**
     * The sound of a super bat.
     */
    private static final String BAT_SOUND = "You hear screeching nearby.\n";

    /**
     * The smell of a wumpus in the adjacent room.
     */
    private static final String WUMPUS_SMELL = "You smell a wumpus nearby!\n";

    /**
     * Creates a new transit service with the given entity store.
     *
     * @param s
     *              the entity store to be used by this service
     */
    public TransitService(final EntityStore s) {
        this.store = s;
    }

    @Override
    public void tick() {
        store.stream().components(Set.of(Transit.class, Physical.class))
                .forEach(m -> {
                    final Entity e = m.getEntity();
                    final Transit t = m.getByComponent(Transit.class);

                    t.getFrom().ifPresent(from -> {
                        removeFromContainer(e, store.get(from).get());
                    });

                    putInContainer(e, store.get(t.getTo()).get());

                    if (e.hasComponent(Wumpus.class)) {
                        wumpusMove(t);
                    }

                    e.deregisterComponent(Transit.class);
                    store.commit(e);

                });
    }

    /**
     * Remove an entity from a container.
     *
     * @param e
     *                 the entity to remove
     * @param from
     *                 the entity of the container to remove from
     */
    private void removeFromContainer(final Entity e, final Entity from) {
        e.registerComponent(new Physical());
        final Container fromC = from.getComponent(Container.class);
        final Predicate<Long> rem = (l -> l != e.getId());
        from.registerComponent(new Container(fromC, rem));
        store.commit(from);
    }

    /**
     * Put an entity in a container.
     *
     * @param e
     *               the entity to put
     * @param to
     *               the entity of the container to put in to
     */
    private void putInContainer(final Entity e, final Entity to) {
        e.registerComponent(new Physical(to.getId()));
        final Container toC = to.getComponent(Container.class);
        to.registerComponent(new Container(toC, e.getId()));
        store.commit(to);
        store.commit(e);

        if (e.hasComponent(Player.class) && to.hasComponent(Room.class)) {
            playerEnter(e, to);
        }
    }

    /**
     * Describe a player room entry to the player and cause entry triggers.
     *
     * @param player
     *                   entity of the player
     * @param to
     *                   the room the player is entering
     */
    private void playerEnter(final Entity player, final Entity to) {
        if (player.hasComponent(Listener.class)) {
            final StringBuilder exs = new StringBuilder();

            exs.append("\n" + "You have entered "
                    + to.getComponent(Descriptive.class).getShortDescription()
                    + ".\n");

            final Map<Direction, Long> exits = to.getComponent(Room.class)
                    .getLinkedRooms();
            if (!exits.isEmpty()) {

                exs.append("You see the following exits:\n");
                exs.append(exits.entrySet().stream()
                        .map(e -> TextTools.capitalize(e.getKey().name())
                                + " - "
                                + store.get(e.getValue()).get()
                                        .getComponent(Descriptive.class)
                                        .getShortDescription())
                        .collect(Collectors.joining("\n")));
                exs.append("\n");
            }

            to.getComponent(Container.class).getContents().stream()
                    .map(id -> store.get(id).get())
                    .collect(EntityStream.collector())
                    .component(Descriptive.class).map(d -> {
                        if (d.getEntity().get().hasComponent(Dead.class)) {
                            return "The corpse of " + d.getShortDescription();
                        } else {
                            return d.getShortDescription();
                        }
                    }).map(d -> TextTools.capitalize(d))
                    .forEach(d -> exs.append(d + " is here.\n"));

            if (!exits.isEmpty()) {
                final Set<Entity> contents = exits.entrySet().stream()
                        .flatMap(e -> store.get(e.getValue()).get()
                                .getComponent(Container.class).getContents()
                                .stream())
                        .map(l -> store.get(l).get())
                        .collect(Collectors.toSet());
                if (contents.stream().filter(e -> e.hasComponent(Wumpus.class))
                        .findAny().isPresent()) {
                    exs.append(WUMPUS_SMELL);
                }
                if (contents.stream()
                        .filter(e -> e.hasComponent(SuperBat.class)).findAny()
                        .isPresent()) {
                    exs.append(BAT_SOUND);
                }
            }

            player.getComponent(Listener.class).tell(exs.toString());
        }
    }

    /**
     * Process special actions for wumpus movement.
     *
     * @param t
     *              the location the wumpus is moving to
     */
    private void wumpusMove(final Transit t) {
        if (t.getFrom().isPresent()) {
            store.stream().components(Set.of(Player.class, Listener.class))
                    .map(cm -> cm.getByComponent(Listener.class))
                    .forEach(l -> l.tell(WUMPUS_MOVE));
        }

        store.get(t.getTo()).get().getComponent(Room.class).getLinkedRooms()
                .values().stream().map(id -> store.get(id).get())
                .flatMap(e -> e.getComponent(Container.class).getContents()
                        .stream())
                .map(id -> store.get(id).get())
                .collect(EntityStream.collector())
                .components(Set.of(Player.class, Listener.class))
                .map(cm -> cm.getByComponent(Listener.class))
                .forEach(l -> l.tell(WUMPUS_SMELL));

    }

}
