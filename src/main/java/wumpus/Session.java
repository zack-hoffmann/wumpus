package wumpus;

import java.io.IOException;

@FunctionalInterface
public interface Session {

    public static Session create(
            final org.eclipse.jetty.websocket.api.Session wrappedSession) {
        return () -> wrappedSession;
    }

    org.eclipse.jetty.websocket.api.Session unwrap();

    default boolean isOpen() {
        return unwrap().isOpen();
    }

    default void send(final String s) throws IOException {
        unwrap().getRemote().sendString(s);
    }
}