package wumpus.system;

import java.util.function.Predicate;
import java.util.stream.Stream;

import wumpus.Entity;

@FunctionalInterface
public interface Step {

    Entity unqualifiedExecute(final Entity e);

    default Predicate<Entity> qualifier() {
        return e -> true;
    }

    default Stream<Entity> execute(final Stream<Entity> es) {
        return es.filter(qualifier()).map(this::unqualifiedExecute);
    }

}