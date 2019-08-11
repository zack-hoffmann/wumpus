package wumpus.io;

/**
 * Represents a connection to the game.
 */
public final class Session {

    /**
     * The entity ID associated with the session.
     */
    private final long entityId;

    /**
     * The IO adapter associated with the session.
     */
    private final IOAdapter io;

    /**
     * Create a new session.
     *
     * @param e
     *              the ID of the entity for the session
     * @param i
     *              the IO for the session
     */
    public Session(final long e, final IOAdapter i) {
        this.entityId = e;
        this.io = i;
    }

    /**
     * Get the entity ID associated with the session.
     *
     * @return the entity ID
     */
    public long entityId() {
        return entityId;
    }

    /**
     * Get the IO for the session.
     *
     * @return the IO adapter
     */
    public IOAdapter io() {
        return io;
    }

    /**
     * Determines if the session is open.
     *
     * @return true if the session is open
     */
    public boolean isOpen() {
        return io.isOpen();
    }
}
