package wumpus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Pool for web socket sessions.
 */
@FunctionalInterface
public interface SessionPool {

    /**
     * Create a session pool.
     *
     * @param app
     *                the app associated with this pool
     * @return the new session pool
     */
    static SessionPool create(final App app) {
        final Map<String, Session> m = new HashMap<>();
        return s -> {
            if (s.isPresent()) {
                final Map<String, Session> newEntry = new HashMap<>();
                newEntry.put(app.tokenPool().newToken(), s.get());
                m.putAll(newEntry);
                return newEntry;
            } else {
                return m;
            }
        };
    }

    /**
     * If a session is provided then generate a token for it, store that token
     * and session, and return a map containing only that new pool entry.
     * Otherwise return the map of the entire pool.
     *
     * @param s
     *              a new session to store
     * @return a map containing only the provided session and its new token, or
     *         the entire pool map
     */
    Map<String, Session> bind(Optional<Session> s);

    /**
     * Obtain a map of all tokens with their pool sessions.
     *
     * @return a map of tokens to sessions
     */
    default Map<String, Session> map() {
        return bind(Optional.empty());
    }

    /**
     * Store a new session and generate a token for it.
     *
     * @param s
     *              the new session to store
     * @return the new session token
     */
    default String register(final Session s) {
        return bind(Optional.of(s)).entrySet().iterator().next().getKey();
    }

    /**
     * Issue a new token for the session if present.
     *
     * @param token
     *                  the current session token
     * @return the new session token, otherwise empty
     */
    default Optional<String> renew(final String token) {
        return session(token).map(ses -> {
            map().remove(token);
            return register(ses);
        });
    }

    /**
     * Obtain a session if present.
     *
     * @param token
     *                  the token of the session to obtain
     * @return the session if present, otherwise empty
     */
    default Optional<Session> session(final String token) {
        return Optional.ofNullable(map().get(token));
    }

    /**
     * Remove all closed sessions from the pool.
     */
    default void clean() {
        final List<String> expired = map().entrySet().stream()
                .filter(es -> !es.getValue().isOpen()).map(es -> es.getKey())
                .collect(Collectors.toList());
        expired.stream().forEach(s -> map().remove(s));

    }
}
