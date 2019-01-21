package wumpus;

import java.io.PrintStream;
import java.util.Set;
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

        store.stream().components(Set.of(Player.class, Physical.class))
                .map(m -> m.getByComponent(Physical.class))
                .filter(p -> p.getLocation().isEmpty())
                .map(p -> p.getEntity().get())
                .forEach(e -> e.registerComponent(new Transit(lairEntrance)));

        store.stream().components(Set.of(Transit.class, Physical.class))
                .forEach(m -> {
                    final Entity e = m.getByComponent(Physical.class)
                            .getEntity().get();
                    final long eid = e.getId();
                    final Transit t = m.getByComponent(Transit.class);
                    final Entity toE = store.get(t.getTo()).get();

                    t.getFrom().ifPresent(from -> {
                        final Entity fromE = store.get(from).get();
                        final Container fromC = fromE
                                .getComponent(Container.class);
                        final Predicate<Long> rem = (l -> l != eid);
                        fromE.registerComponent(new Container(fromC, rem));
                        store.commit(fromE);
                    });

                    e.registerComponent(new Physical(t.getTo()));
                    store.commit(e);

                    final Container toC = toE.getComponent(Container.class);
                    toE.registerComponent(new Container(toC, eid));
                    store.commit(toE);
                });
    }
}
