package wumpus;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;

@FunctionalInterface
public interface SessionPool {

    final class SessionPoolHeap {
        private static final Map<String, SessionPool> INST = new HashMap<>();
    }

    static SessionPool recall(final Context ctx, final String name) {
        return SessionPoolHeap.INST.computeIfAbsent(
                StringPool.tokenPool(ctx).intern(name),
                n -> (() -> new HashMap<String, Session>()));
    }

    Map<String, Session> map();

    default void clean() {

    }
}