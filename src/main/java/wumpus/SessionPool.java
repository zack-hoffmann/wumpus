package wumpus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jetty.websocket.api.Session;

/**
 * Pool for web socket sessions.
 */
@FunctionalInterface
public interface SessionPool {

    /**
     * Wrapper for allocating and concealing the session pool space.
     */
    final class SessionPoolHeap {
        /**
         * Map of session pool names to instances.
         */
        private static final Map<String, SessionPool> INST = new HashMap<>();
    }

    /**
     * Obtain a pool by name, or create it if no such pool exists.
     *
     * @param ctx
     *                 application context for the pool
     * @param name
     *                 the name of the pool to obtain
     */
    static SessionPool recall(final Context ctx, final String name) {
        return SessionPoolHeap.INST.computeIfAbsent(
                StringPool.tokenPool(ctx).intern(name), n -> HashMap::new);
    }

    /**
     * Helper to recall pool for initial sessions.
     *
     * @param ctx
     *                application context for the pool
     * @return the initial session pool
     */
    static SessionPool initialPool(final Context ctx) {
        return recall(ctx, ctx.requiredProperty("session.pool.initial.name"));
    }

    /**
     * Helper to recall pool for player sessions.
     *
     * @param ctx
     *                application context for the pool
     * @return the player session pool
     */
    static SessionPool playerPool(final Context ctx) {
        return recall(ctx, ctx.requiredProperty("session.pool.player.name"));
    }

    /**
     * Helper to recall pool for character sessions.
     *
     * @param ctx
     *                application context for the pool
     * @return the character session pool
     */
    static SessionPool characterPool(final Context ctx) {
        return recall(ctx, ctx.requiredProperty("session.pool.character.name"));
    }

    /**
     * Find a session for the token, resolving character session first, then
     * player sessions, then initial sessions.
     *
     * @param ctx
     *                  application context for the pools
     * @param token
     *                  the token for the session to obtain
     * @return reference to the session if present
     */
    static Optional<Session> resolveTokenToSession(final Context ctx,
            final String token) {
        return characterPool(ctx).session(token, ctx)
                .or(() -> playerPool(ctx).session(token, ctx))
                .or(() -> initialPool(ctx).session(token, ctx));
    }

    /**
     * Clean all session pools.
     */
    static void cleanAll() {
        SessionPoolHeap.INST.values().stream().forEach(SessionPool::clean);
    }

    /**
     * Return the underlying map for the pool.
     *
     * @return the session map for the pool
     */
    Map<String, Session> map();

    default String register(final Session s, final Context ctx) {
        final String token = StringPool.tokenPool(ctx).newToken();
        map().put(token, s);
        return token;
    }

    default String renew(final String token, final Context ctx) {
        final Session s = map().get(token);
        map().remove(token);
        return register(s, ctx);
    }

    default Optional<Session> session(final String token, final Context ctx) {
        return Optional.ofNullable(map().get(token));
    }

    default void clean() {
        final List<String> expired = map().entrySet().stream()
                .filter(es -> !es.getValue().isOpen()).map(es -> es.getKey())
                .collect(Collectors.toList());
        expired.stream().forEach(s -> map().remove(s));

    }
}