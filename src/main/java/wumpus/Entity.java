package wumpus;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import wumpus.external.EntityRepository;

@FunctionalInterface
public interface Entity {

    static Supplier<Entity> create = () -> EntityRepository.create.get();
    static Function<Token, Optional<Entity>> of = t -> EntityRepository.lookup
            .apply(t);

    Token token();

    default Set<Component<?>> components() {
        return EntityRepository.lookupComponents.apply(token());
    }

    default void registerComponent(final Component<?> c) {
        EntityRepository.registerComponent.apply(token(), c);
    }
}