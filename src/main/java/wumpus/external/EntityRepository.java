package wumpus.external;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import wumpus.Entity;
import wumpus.Token;
import wumpus.component.Component;

public interface EntityRepository {

    class ComponentMap
            extends ConcurrentHashMap<Class<? extends Component>, Component> {

        private static final long serialVersionUID = 1L;

    }

    // TODO for dev purposes
    static Map<String, ComponentMap> memstore = new ConcurrentHashMap<>();

    static Supplier<ComponentMap> createComponentMap = ComponentMap::new;

    static Function<Token, Optional<Entity>> lookup = t -> Optional
            .of(memstore.keySet().contains(t.string())).filter(b -> b)
            .map(b -> (() -> t));

    static Function<Token, Token> storeToken = t -> Optional
            .of(memstore.put(t.string(), createComponentMap.get())).map(s -> t)
            .get();

    static Supplier<Entity> create = () -> storeToken.andThen(lookup)
            .apply(Token.create.get()).get();

    static Function<Token, Collection<Component>> lookupComponents = t -> Collections
            .unmodifiableCollection(
                    memstore.getOrDefault(t.string(), createComponentMap.get())
                            .values());

    static BiFunction<Token, Component, Component> registerComponent = (t,
            c) -> memstore.get(t.string()).put(c.getClass(), c);

    static BiFunction<Token, Class<? extends Component>, Boolean> hasComponent = (
            t, c) -> memstore.get(t.string()).containsKey(c);
}