package wumpus;

import java.util.function.Function;
import java.util.function.Supplier;

import wumpus.external.StringPool;

@FunctionalInterface
public interface Token {

    static Supplier<StringPool> pool = () -> StringPool
            .construct(Integer.parseInt(
                    Application.instance.requiredProperty("token.pool.size")));

    static Supplier<Token> create = () -> pool.get()::newToken;

    static Function<String, Token> of = s -> (() -> pool.get().intern(s));

    static Supplier<Token> none = () -> of.apply("");

    String string();

}