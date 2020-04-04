package wumpus.external;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

@FunctionalInterface
public interface ServerLog {
    Supplier<ServerLog> create = () -> (l, s) -> Logger.getGlobal().log(l, s);

    void log(final Level l, final String s);

    default void info(final String s) {
        log(Level.INFO, s);
    }

    default void crash(final String s) {
        log(Level.SEVERE, s);
    }

    default void detail(final String s) {
        log(Level.FINE, s);
    }
}