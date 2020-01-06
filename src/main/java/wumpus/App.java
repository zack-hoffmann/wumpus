package wumpus;

import java.util.logging.Logger;

/**
 * Application instance for the wumpus game.
 */
@FunctionalInterface
public interface App extends Runnable {

    /**
     * Runs a new wumpus App instance using standard output.
     *
     * @param args
     *                 Command line arguments. The one and only command line
     *                 argument must be the name of the properties file to use.
     */
    static void main(final String... args) {
        // TODO start in new thread
        App.create(Context.create(args[0])).run();
    }

    /**
     * Create a new app instance based on the given context.
     *
     * @param ctx
     *                the immutable app context to use
     * @return the new app, not yet running
     */
    static App create(final Context ctx) {
        return () -> ctx;
    }

    /**
     * Get the original context for the app.
     *
     * @return the original immutable context
     */
    Context context();

    /**
     * Start the app as configured.
     */
    default void run() {
        final Logger LOG = Logger.getLogger(App.class.getName());
        LOG.info("Starting application server for Hunt the Wumpus");
        LOG.fine("Fine logging is enabled.");
    }

    /**
     * Retrieve the string pool for tokens.
     *
     * @return the token string pool
     */
    default StringPool tokenPool() {
        return StringPool.recall(
                context().requiredProperty("string.pool.token.name"),
                Integer.parseInt(
                        context().requiredProperty("string.pool.token.size")));
    }

    /**
     * Retrieve the pool for initial sessions.
     *
     * @return the initial session pool
     */
    default SessionPool initialSessionPool() {
        return SessionPool.recall(this,
                context().requiredProperty("session.pool.initial.name"));
    }

    /**
     * Retrieve the pool for player sessions.
     *
     * @return the player session pool
     */
    default SessionPool playerSessionPool() {
        return SessionPool.recall(this,
                context().requiredProperty("session.pool.player.name"));
    }

    /**
     * Retrieve the pool for character sessions.
     *
     * @return the character session pool
     */
    default SessionPool characterSessionPool() {
        return SessionPool.recall(this,
                context().requiredProperty("session.pool.character.name"));
    }

    default Authenticator authenticator() {
        return (t, u, p) -> Authenticator.Status.UNKNOWN;
    }
}
