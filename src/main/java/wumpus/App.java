package wumpus;

import java.io.PrintStream;
import java.util.Optional;
import java.util.function.Predicate;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.MemoryEntityStore;
import wumpus.engine.entity.component.Container;
import wumpus.engine.entity.component.Lair;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
import wumpus.engine.entity.component.Transit;
import wumpus.engine.service.LairService;
import wumpus.engine.service.PlayerService;

/**
 * Application instance for the wumpus game.
 */
public class App implements Runnable {

    /**
     * Output stream used by this App instance.
     */
    private final PrintStream out;

    /**
     * Runs a new wumpus App instance using standard output.
     *
     * @param args
     *                 Command line arguments. None specified at this time.
     */
    public static void main(final String... args) {
        new App(System.out).run();
    }

    /**
     * Set up a new instance of the wumpus App with an output stream.
     *
     * @param o
     *              A print stream for use by App output.
     */
    public App(final PrintStream o) {
        this.out = o;
    }

    @Override
    public final void run() {
        out.println("Welcome to Hunt the Wumpus by Zack Hoffmann!");

        final EntityStore store = new MemoryEntityStore();
        final LairService lairs = new LairService(store);
        final PlayerService players = new PlayerService(store);
        lairs.createLair(1);
        players.createPlayer();
        final long lairEntrance = store.stream().component(Lair.class)
                .findFirst().orElseThrow().getEntrace();
        store.stream().filter(e -> e.hasComponent(Player.class))
                .filter(e -> e.hasComponent(Physical.class))
                .filter(p -> p.getComponent(Physical.class).getLocation()
                        .isEmpty())
                .forEach(p -> p.registerComponent(new Transit(lairEntrance)));
        store.stream().filter(e -> e.hasComponent(Transit.class))
                .filter(e -> e.hasComponent(Physical.class)).forEach(e -> {
                    final Transit tr = e.getComponent(Transit.class);
                    final Entity toE = store.get(tr.getTo()).orElseThrow();
                    final Optional<Long> fromEId = tr.getFrom();
                    e.registerComponent(new Physical(toE.getId()));
                    if (toE.hasComponent(Container.class)) {
                        final Container toC = toE.getComponent(Container.class);
                        toE.registerComponent(new Container(toC, e.getId()));
                    }
                    if (fromEId.isPresent()) {
                        final Entity fromE = store.get(fromEId.get())
                                .orElseThrow();
                        if (fromE.hasComponent(Container.class)) {
                            final Container fromC = fromE
                                    .getComponent(Container.class);
                            final Predicate<Long> rem = (l -> l != e.getId());
                            fromE.registerComponent(new Container(fromC, rem));
                        }
                    }
                });
    }
}
