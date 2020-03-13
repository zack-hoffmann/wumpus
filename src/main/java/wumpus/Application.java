package wumpus;

import java.util.Optional;
import java.util.Properties;

import wumpus.system.Utilities;

@FunctionalInterface
public interface Application extends Runnable {

    static void main(final String... args) {
        Application.get().run();
    }

    static Application get() {
        return s -> Utilities.loadProperties.andThen(p -> p.getProperty(s))
                .andThen(Optional::ofNullable).apply(new Properties());
    }

    /**
     * Obtain a possible reference to the property.
     *
     * @param propertyName
     *                         the name of the property to obtain
     * @return an optional reference to the property value or an empty optional
     *         if not found
     */
    Optional<String> property(String propertyName);

    /**
     * Obtain a property value or crash.
     *
     * @param propertyName
     *                         the name of the property to obtain
     * @return the property value
     */
    default String requiredProperty(String propertyName) {
        return Mediator.require(() -> this.property(propertyName).get());
    }

    @Override
    default void run() {
    }
}