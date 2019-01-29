package wumpus.io;

/**
 * Exception caused by an error with game I/O.
 */
public class GameIOException extends RuntimeException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3514580194025718392L;

    /**
     * Construct with detail message.
     *
     * @param message
     *                    exception detail message
     */
    public GameIOException(final String message) {
        super(message);
    }

    /**
     * Construct with detail message and cause.
     *
     * @param message
     *                    exception detail message
     * @param cause
     *                    exception cause
     */
    public GameIOException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
