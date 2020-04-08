package wumpus.system;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import wumpus.Entity;

@FunctionalInterface
public interface System {

    List<Step> steps();

    default UnaryOperator<List<Entity>> stepChain() {
        return el -> steps().stream().map(s -> s.execute(el.stream()))
                .flatMap(Function.identity()).collect(Collectors.toList());
    }

    default List<Entity> execute(final List<Entity> es) {
        return stepChain().apply(es);
    }

}