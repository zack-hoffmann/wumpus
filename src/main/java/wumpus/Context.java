package wumpus;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the immutable state in which the application was loaded.
 */
public interface Context {

    /**
     * Logger.
     */
    Logger LOG = Logger.getLogger(App.class.getName());

    /**
     * Application properties file name.
     */
    String PROP_FILE_NAME = "wumpus.properties";

    /**
     * Create a context on a map of strings.
     *
     * @return the context view of that map
     */
    static Context create() {
        return s -> Optional.ofNullable(loadProperties().getProperty(s));
    }

    /**
     * Load the application properties.
     *
     * @return the loaded application properties
     */
    private static Properties loadProperties() {
        LOG.info("Loading properties...");

        final Properties p = new Properties();
        rundirPropertiesFile().ifPresent(f -> loadFileToProperties(p, f));
        loadFileToProperties(p, defaultPropertiesFile());

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Loaded properties: " + p);
        }
        return p;
    }

    /**
     * Obtain reference to default application properties. The file must exist
     * at the base of the classpath.
     *
     * @return reference to default properties file
     */
    private static InputStream defaultPropertiesFile() {
        return Mediator.require(() -> {
            return Context.class.getResourceAsStream("/" + PROP_FILE_NAME);
        });
    }

    /**
     * Obtain reference to run directory properties. Will check for the optional
     * file in the current user directory.
     *
     * @return reference to the run directory properties file
     */
    private static Optional<InputStream> rundirPropertiesFile() {
        return Mediator.attempt(() -> {
            return Files
                    .newInputStream(Paths.get(System.getProperty("user.dir"))
                            .resolve(PROP_FILE_NAME));
        });
    }

    /**
     * Attempts to load a properties file in to the properties object.
     *
     * @param p
     *              the properties object
     * @param f
     *              the file reference
     */
    private static void loadFileToProperties(final Properties p,
            final InputStream f) {

        Mediator.attempt((fp) -> {
            try (InputStream is = fp) {
                p.load(is);
            }
        }, f);

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
}
