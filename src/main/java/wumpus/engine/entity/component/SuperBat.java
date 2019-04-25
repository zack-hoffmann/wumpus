package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Describes a super bat entity, capable of moving players to a random room.
 */
public final class SuperBat extends AbstractEntityComponent {

    /**
     * Initial location of the bat.
     */
    private final long location;

    /**
     * Create a super bar with initial location.
     *
     * @param l
     *              initial location
     */
    public SuperBat(final long l) {
        this.location = l;
    }

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Physical(location), new Hazard(), new Descriptive(
                "a super bat", "a massive bat with an ear-piercing screech"));
    }
}
