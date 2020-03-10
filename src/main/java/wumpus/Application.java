package wumpus;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

@FunctionalInterface
public interface Application extends Runnable {

    public static final String PROP_FILE = "wumpus.properties";

    static void main(final String... args) {
        Application.get().run();
    }

    static Application get() {
        final Properties p = new Properties();
        Mediator.require(n -> {
            p.load(Application.class.getResourceAsStream("/" + n));
            p.load(Files.newInputStream(
                    Paths.get(System.getProperty("user.dir")).resolve(n)));
        }, PROP_FILE);
        return s -> Optional.ofNullable(p.getProperty(s));
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