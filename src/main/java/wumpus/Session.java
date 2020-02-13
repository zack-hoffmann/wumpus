package wumpus;

import java.io.IOException;

/**
 * A wrapper session to more easily interact with native sessions.
 */
@FunctionalInterface
public interface Session {

    /**
     * Exceptions from using sessions.
     */
    class SessionException extends RuntimeException {

        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Generate an exception for not being able to send a string.
         *
         * @param cause
         *                  the exception which prompted this one
         * @return the new exception
         */
        private static SessionException couldNotSend(final Exception cause) {
            return new SessionException("Could not send string.", cause);
        }

        /**
         * Private constructor.
         *
         * @param message
         *                    message for the exception
         * @param cause
         *                    cause for the exception
         */
        private SessionException(final String message, final Exception cause) {
            super(message, cause);
        }
    }

    /**
     * Create a wrapper around a native session.
     *
     * @param wrappedSession
     *                           the native session to wrap
     * @return the wrapper session
     */
    static Session create(
            final org.eclipse.jetty.websocket.api.Session wrappedSession) {
        return () -> wrappedSession;
    }

    /**
     * Obtain the native session.
     *
     * @return the native wrapped session
     */
    org.eclipse.jetty.websocket.api.Session unwrap();

    /**
     * Check if the session is open.
     *
     * @return true if open, false otherwise
     */
    default boolean isOpen() {
        return unwrap().isOpen();
    }

    /**
     * Send a string to the remote receive of this session.
     *
     * @param s
     *              the string to send
     * @throws SessionException
     *                              if the string could not be sent
     */
    default void send(final String s) {
        try {
            unwrap().getRemote().sendString(s);
        } catch (IOException e) {
            throw SessionException.couldNotSend(e);
        }

    }
}
