package wumpus;

import java.util.logging.Logger;

/**
 * Application instance for the wumpus game.
 */
public final class App {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(App.class.getName());

    /**
     * Runs a new wumpus App instance using standard output.
     *
     * @param args
     *                 Command line arguments. None specified at this time.
     */
    public static void main(final String... args) {
        LOG.info("Starting application server for Hunt the Wumpus");
        LOG.fine("Fine logging is enabled.");

        final Context ctx = Context.create();
        ctx.property("hello");
    }

    /**
     * Private constructor.
     */
    private App() {
        LOG.warning("This class should not be initialized.");
    }
}
