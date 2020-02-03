package wumpus;

import java.util.HashMap;
import java.util.Map;
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
        final Map<String, Object> mr = new HashMap<>();
        mr.put("CONTEXT", ctx);
        return () -> mr;
    }

    /**
     * Get the memory root for the app. Not to be accessed directly!
     *
     * @return a map which holds all references to all application objects
     */
    Map<String, Object> memoryRoot();

    /**
     * Get the original context for the app.
     *
     * @return the original immutable context
     */
    default Context context() {
        return (Context) memoryRoot().get("CONTEXT");
    }

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
        return (StringPool) memoryRoot().computeIfAbsent(
                context().requiredProperty("string.pool.token.name"),
                n -> StringPool.construct(Integer.parseInt(
                        context().requiredProperty("string.pool.token.size"))));
    }

    default StringPool parameterPool() {
        return (StringPool) memoryRoot().computeIfAbsent(
                context().requiredProperty("string.pool.param.name"),
                n -> StringPool.construct(Integer.parseInt(
                        context().requiredProperty("string.pool.param.size"))));
    }

    /**
     * Retrieve the pool for initial sessions.
     *
     * @return the initial session pool
     */
    default SessionPool initialSessionPool() {
        return (SessionPool) memoryRoot().computeIfAbsent(
                "INITIAL_SESSION_POOL", n -> SessionPool.create(this));
    }

    /**
     * Retrieve the pool for player sessions.
     *
     * @return the player session pool
     */
    default SessionPool playerSessionPool() {
        return (SessionPool) memoryRoot().computeIfAbsent("PLAYER_SESSION_POOL",
                n -> SessionPool.create(this));
    }

    /**
     * Retrieve the pool for character sessions.
     *
     * @return the character session pool
     */
    default SessionPool characterSessionPool() {
        return (SessionPool) memoryRoot().computeIfAbsent(
                "CHARACTER_SESSION_POOL", n -> SessionPool.create(this));
    }

    default Authenticator authenticator() {
        final Authenticator a = (t, u, p) -> Authenticator.Status.UNKNOWN;
        return (Authenticator) memoryRoot().computeIfAbsent("AUTHENTICATOR",
                n -> a);
    }
}
