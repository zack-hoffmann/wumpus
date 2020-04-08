package wumpus.component;

import java.util.Optional;

import wumpus.Entity;
import wumpus.Token;
import wumpus.external.EntityCache;

@FunctionalInterface
public interface EntityReferenceComponent extends ValueComponent<Token> {

    default Optional<Entity> entity() {
        return Entity.of.apply(value());
    }
}