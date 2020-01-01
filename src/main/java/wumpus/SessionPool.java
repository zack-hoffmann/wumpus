package wumpus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jetty.websocket.api.Session;

@FunctionalInterface
public interface SessionPool {

    final class SessionPoolHeap {
        private static final Map<String, SessionPool> INST = new HashMap<>();
    }

    static SessionPool recall(final Context ctx, final String name) {
        return SessionPoolHeap.INST.computeIfAbsent(
                StringPool.tokenPool(ctx).intern(name), n -> HashMap::new);
    }

    static SessionPool initialPool(final Context ctx) {
        return recall(ctx, ctx.requiredProperty("session.pool.initial.name"));
    }

    static SessionPool playerPool(final Context ctx) {
        return recall(ctx, ctx.requiredProperty("session.pool.player.name"));
    }

    static SessionPool characterPool(final Context ctx) {
        return recall(ctx, ctx.requiredProperty("session.pool.character.name"));
    }

    static Optional<Session> resolveTokenToSession(final Context ctx,
            final String token) {
        return characterPool(ctx).session(token, ctx)
                .or(() -> playerPool(ctx).session(token, ctx))
                .or(() -> initialPool(ctx).session(token, ctx));
    }

    static void cleanAll() {
        SessionPoolHeap.INST.values().stream().forEach(SessionPool::clean);
    }

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