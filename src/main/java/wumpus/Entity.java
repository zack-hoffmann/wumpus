package wumpus;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import wumpus.component.Component;
import wumpus.external.EntityCache;

@FunctionalInterface
public interface Entity {

    static Function<EntityCache, Entity> create = ec -> () -> EntityCache.forNewEntity
            .apply(ec);
    static BiFunction<EntityCache, Token, Optional<Entity>> of = (ec, t) -> ec
            .lookup(t);

    EntityCache cache();

    default Token token() {
        return cache().memory().keySet().stream().findFirst()
                .map(s -> Token.of.apply(s)).get();
    }

    default Collection<Component> components() {
        return cache().lookupComponents(token());
    }

    default void registerComponent(final Component c) {
        cache().registerComponent(token(), c);
    }

    default boolean isA(Class<? extends Component> c) {
        return cache().hasComponent(token(), c);
    }
}