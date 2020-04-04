package wumpus.component;

import java.util.Optional;

import wumpus.Entity;

public interface Session extends EntityReferenceComponent {

    default Optional<Entity> player() {
        return entity().filter(e -> e.isA(Player.class));
    }
}