package wumpus.engine.entity.component;

/**
 * For entities that can be described to the player.
 */
public final class Descriptive extends AbstractEntityComponent {

    /**
     * The brief description of the entity.
     *
     * Should contain an article (a, an, the), any number of adjectives, and a
     * noun.
     */
    private final String shortDescription;

    /**
     * A detailed description of what the entity looks like.
     */
    private final String longDescription;

    /**
     * Create a descriptive component with differing short and long
     * descriptions.
     *
     * @param sd
     *               a short description of the entity
     * @param ld
     *               a detailed description of the entity
     */
    public Descriptive(final String sd, final String ld) {
        this.shortDescription = sd;
        this.longDescription = ld;
    }

    /**
     * Create a descriptive component with the same short and long descriptions.
     *
     * @param d
     *              a description of the entity
     */
    public Descriptive(final String d) {
        this.shortDescription = d;
        this.longDescription = d;
    }

    /**
     * Get the short description.
     *
     * @return a short description of the entity
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Get the detailed description.
     *
     * @return a detailed description of the entity
     */
    public String getLongDescription() {
        return longDescription;
    }
}
