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
     * @param app
     *                 application instance for the pool
     * @param name
     *                 the name of the pool to obtain
     */
    static SessionPool recall(final App app, final String name) {
        return SessionPoolHeap.INST.computeIfAbsent(
                app.tokenPool().intern(name), n -> HashMap::new);
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

    default String register(final App app, final Session s) {
        final String token = app.tokenPool().newToken();
        map().put(token, s);
        return token;
    }

    default String renew(final App app, final String token) {
        final Session s = map().get(token);
        map().remove(token);
        return register(app, s);
    }

    default Optional<Session> session(final String token) {
        return Optional.ofNullable(map().get(token));
    }

    default void clean() {
        final List<String> expired = map().entrySet().stream()
                .filter(es -> !es.getValue().isOpen()).map(es -> es.getKey())
                .collect(Collectors.toList());
        expired.stream().forEach(s -> map().remove(s));

    }
}