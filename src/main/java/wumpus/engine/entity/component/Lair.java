package wumpus.engine.entity.component;

/**
 * Representing an entity which is a wumpus lair.
 */
public final class Lair extends AbstractEntityComponent {

    /**
     * Entity ID of the entrace room to the lair.
     */
    private final long entrance;

    /**
     * Create the lair with a given entrance.
     *
     * @param e the entity ID of the entrace room
     */
    public Lair(final long e) {
        this.entrance = e;
    }

    /**
     * Create the lair with an illegal entrance.
     */
    public Lair() {
        this(-1L);
    }

    /**
     * Get the entity ID of the entrance room.
     *
     * @return the entity ID of the entrace room
     */
    public long getEntrace() {
        return entrance;
    }

}
