package wumpus;

import java.io.IOException;

@FunctionalInterface
public interface Session {

    class SessionException extends RuntimeException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private static SessionException couldNotSend(final Exception cause) {
            return new SessionException("Could not send string.", cause);
        }

        private SessionException(final String message, final Exception cause) {
            super(message, cause);
        }
    }

    public static Session create(
            final org.eclipse.jetty.websocket.api.Session wrappedSession) {
        return () -> wrappedSession;
    }

    org.eclipse.jetty.websocket.api.Session unwrap();

    default boolean isOpen() {
        return unwrap().isOpen();
    }

    default void send(final String s) {
        try {
            unwrap().getRemote().sendString(s);
        } catch (IOException e) {
            throw SessionException.couldNotSend(e);
        }

    }
}