package wumpus;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import wumpus.component.Component;
import wumpus.external.EntityRepository;

@FunctionalInterface
public interface Entity {

    static Supplier<Entity> create = () -> EntityRepository.create.get();
    static Function<Token, Optional<Entity>> of = t -> EntityRepository.lookup
            .apply(t);

    Token token();

    default Collection<Component<?>> components() {
        return EntityRepository.lookupComponents.apply(token());
    }

    // TODO not a big fan of this...
    default Collection<String> componentTypes() {
        return EntityRepository.lookupComponentTypes.apply(token());
    }

    default void registerComponent(final Component<? extends Object> c) {
        EntityRepository.registerComponent.apply(token(), c);
    }

    default boolean isA(Class<?> c) {
        return componentTypes().contains(c.getName());
    }
}