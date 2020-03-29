package wumpus;

import java.util.function.Function;
import java.util.function.Supplier;

import wumpus.system.StringPool;

@FunctionalInterface
public interface Token {

    static StringPool pool = StringPool.construct(Integer.parseInt(
            Application.instance.requiredProperty("token.pool.size")));

    static Supplier<Token> create = () -> pool::newToken;

    static Function<String, Token> of = s -> (() -> pool.intern(s));

    String string();

}