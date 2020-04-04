package wumpus;

import java.util.Optional;
import java.util.function.Function;

import wumpus.external.Properties;
import wumpus.external.WebServer;

@FunctionalInterface
public interface Application extends Runnable {

    static void main(final String... args) {
        Application.instance.run();
    }

    Function<String,String> propertiesCache = (Properties.create.get()::property);

    Application instance = s -> propertiesCache.andThen(Optional::ofNullable).apply(s);

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
        final WebServer ws = WebServer.create.get();
        ws.start();
        // TODO run systems
        ws.stop();
    }
}