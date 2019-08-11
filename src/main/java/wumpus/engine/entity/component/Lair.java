package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Representing an entity which is a wumpus lair.
 */
public final class Lair extends AbstractEntityComponent {

    /**
     * Entity ID of the entrace room to the lair.
     */
    private final long entrance;

    /**
     * The wumpus that lives in this lair.
     */
    private final long wumpus;

    /**
     * Create the lair with a given entrance and wumpus.
     *
     * @param e
     *              the entity ID of the entrace room
     * @param w
     *              the entity ID of the wumpus
     */
    public Lair(final long e, final long w) {
        this.entrance = e;
        this.wumpus = w;
    }

    /**
     * Get the entity ID of the entrance room.
     *
     * @return the entity ID of the entrace room
     */
    public long entrance() {
        return entrance;
    }

    /**
     * Get the entity ID of the wumpus.
     *
     * @return the entity ID of the wumpus
     */
    public long wumpus() {
        return wumpus;
    }

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Zone());
    }
}
