package wumpus.external;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import wumpus.Component;
import wumpus.Entity;
import wumpus.Token;

public interface EntityRepository {

    // TODO for dev purposes
    static Map<String, Set<Component<?>>> memstore = new ConcurrentHashMap<>();

    static Supplier<Set<Component<?>>> createComponentSet = HashSet::new;

    static Function<Token, Optional<Entity>> lookup = t -> Optional
            .of(memstore.keySet().contains(t.string())).filter(b -> b)
            .map(b -> (() -> t));

    static Function<Token, Token> storeToken = t -> Optional
            .of(memstore.put(t.string(), createComponentSet.get())).map(s -> t)
            .get();

    static Supplier<Entity> create = () -> storeToken.andThen(lookup)
            .apply(Token.create.get()).get();

    static Function<Token, Set<Component<?>>> lookupComponents = t -> memstore
            .getOrDefault(t.string(), createComponentSet.get());

    static BiFunction<Token, Component<?>, Boolean> registerComponent = (t,
            c) -> memstore.get(t.string()).add(c);
}