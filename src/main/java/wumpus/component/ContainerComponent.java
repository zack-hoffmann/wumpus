package wumpus.component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import wumpus.Entity;
import wumpus.Token;

public interface ContainerComponent extends ValueComponent<List<Token>> {

    default List<Entity> entities() {
        return stream().collect(Collectors.toList());
    }

    default Stream<Entity> stream() {
        return value().stream().map(Entity.of).filter(Optional::isPresent)
                .map(Optional::get);
    }
}