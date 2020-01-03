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
                app.tokenPool().intern(name), n -> create(app));
    }

    static SessionPool create(final App app) {
        final Map<String, Session> m = new HashMap<>();
        return s -> bind(m, app, s);
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
    Map<String, Session> bind(final Optional<Session> s);

    static Map<String, Session> bind(final Map<String, Session> map,
            final App app, final Optional<Session> s) {
        if (s.isPresent()) {
            final Map<String, Session> newEntry = new HashMap<>();
            newEntry.put(app.tokenPool().newToken(), s.get());
            map.putAll(newEntry);
            return newEntry;
        } else {
            return map;
        }
    }

    default Map<String, Session> map() {
        return bind(Optional.empty());
    }

    default String register(final Session s) {
        return bind(Optional.of(s)).entrySet().iterator().next().getKey();
    }

    default String renew(final String token) {
        final Session s = map().get(token);
        map().remove(token);
        return register(s);
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