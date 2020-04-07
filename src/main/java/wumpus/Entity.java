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

    default Collection<Component> components() {
        return EntityRepository.lookupComponents.apply(token());
    }

    default void registerComponent(final Component c) {
        EntityRepository.registerComponent.apply(token(), c);
    }

    default boolean isA(Class<? extends Component> c) {
        return EntityRepository.hasComponent.apply(token(), c);
    }
}