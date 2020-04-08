package wumpus.external;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import wumpus.Entity;
import wumpus.Token;
import wumpus.component.Component;

@FunctionalInterface
public interface EntityCache {

    class ComponentMap
            extends ConcurrentHashMap<Class<? extends Component>, Component> {

        private static final long serialVersionUID = 1L;

    }

    static Supplier<ComponentMap> createComponentMap = ComponentMap::new;

    static BiFunction<EntityCache, Predicate<Map.Entry<String, ComponentMap>>, EntityCache> ofSubset = (
            ec, p) -> {
        final Map<String, ComponentMap> m = new AbstractMap<>() {
            @Override
            public Set<Entry<String, ComponentMap>> entrySet() {
                return ec.memory().entrySet().stream().filter(p)
                        .collect(Collectors.toUnmodifiableSet());
            }
        };

        return () -> m;
    };

    static Function<EntityCache, EntityCache> of = ec -> ofSubset.apply(ec,
            q -> true);

    static BiFunction<EntityCache, Token, EntityCache> forEntity = (ec,
            t) -> ofSubset.apply(ec, q -> q.getKey().equals(t.string()));

    static Function<EntityCache, EntityCache> forNewEntity = ec -> forEntity
            .apply(ec, ec.create().token());

    Map<String, ComponentMap> memory();

    default Optional<Entity> lookup(final Token t) {
        return Optional.of(() -> forEntity.apply(this, t));
    }

    default Token storeToken(final Token t) {
        return Optional.of(memory().put(t.string(), createComponentMap.get()))
                .map(s -> t).get();
    }

    default Entity create() {
        return lookup(storeToken(Token.create.get())).get();
    }

    default Collection<Component> lookupComponents(final Token t) {
        return Collections.unmodifiableCollection(memory()
                .computeIfAbsent(t.string(), s -> createComponentMap.get())
                .values());
    }

    default Component registerComponent(final Token t, final Component c) {
        return memory().get(t.string()).put(c.getClass(), c);
    }

    default boolean hasComponent(final Token t,
            final Class<? extends Component> c) {
        return memory().get(t.string()).containsKey(c);
    }

}