package wumpus.system;

import java.util.function.Predicate;
import java.util.stream.Stream;

import wumpus.Entity;
import wumpus.external.Mediator;

@FunctionalInterface
public interface Step {

    Entity execute(final Entity e) throws Mediator.ServiceError;

    default Predicate<Entity> qualifier() {
        return e -> true;
    }

    default Stream<Entity> streamExecute(final Stream<Entity> es) {
        return es.filter(qualifier()).map(this::execute);
    }

}