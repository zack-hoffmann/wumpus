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
     * Application properties file name.
     */
    private static final String PROP_FILE_NAME = "wumpus.properties";

    /**
     * Runs a new wumpus App instance using standard output.
     *
     * @param args
     *                 Command line arguments. None specified at this time.
     */
    public static void main(final String... args) {
        LOG.info("Starting application server for Hunt the Wumpus");
        LOG.fine("Fine logging is enabled.");

        final Context ctx = Context.create(PROP_FILE_NAME);
        ctx.property("hello");
    }

    /**
     * Private constructor.
     */
    private App() {
        LOG.warning("This class should not be initialized.");
    }
}
