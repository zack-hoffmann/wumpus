package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Describes a super bat entity, capable of moving players to a random room.
 */
public final class SuperBat extends AbstractEntityComponent {

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Physical(), new Hazard(), new Descriptive(
                "a super bat", "a massive bat with an ear-piercing screech"));
    }
}
