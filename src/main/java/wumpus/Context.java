package wumpus;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Represents the immutable state in which the application was loaded.
 */
public interface Context {

    /**
     * Create a context on a map of strings.
     *
     * @param p
     *              supplier of properties from which the context should be
     *              based
     * @return the context view of that map
     */
    static Context create(final Supplier<Properties> p) {
        return s -> Optional.ofNullable(p.get().getProperty(s));
    }

    /**
     * Obtain a possible reference to the property.
     *
     * @param propertyName
     *                         the name of the property to obtain
     * @return an optional reference to the property value or an empty optional
     *         if not found
     */
    Optional<String> property(final String propertyName);
}
