package wumpus;

import java.io.PrintStream;

import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.MemoryEntityStore;
import wumpus.engine.entity.component.Physical;
import wumpus.engine.entity.component.Player;
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
        store.getAll()
            .filter(e -> e.hasComponent(Player.class))
            .filter(e -> e.hasComponent(Physical.class))
            .filter(p -> p.getComponent(Physical.class).getLocation()
                .isEmpty())
            .forEach(e -> System.out.println("Entity " + e.getId()
                + " is a player entity without a location!"));
    }
}
