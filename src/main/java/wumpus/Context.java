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
@FunctionalInterface
public interface Context {

    /**
     * Logger.
     */
    Logger LOG = Logger.getLogger(Context.class.getName());

    /**
     * Create a context on a map of strings.
     *
     * @param fileName
     *                     name for the context properties file
     * @return the context view of that map
     */
    static Context create(final String fileName) {
        return s -> Optional
                .ofNullable(loadProperties(fileName).getProperty(s));
    }

    /**
     * Load the application properties.
     *
     * @param fileName
     *                     name for the context properties file
     * @return the loaded application properties
     */
    private static Properties loadProperties(final String fileName) {
        LOG.info("Loading properties...");

        final Properties p = new Properties();
        loadFileToProperties(p, defaultPropertiesFile(fileName));
        rundirPropertiesFile(fileName)
                .ifPresent(f -> loadFileToProperties(p, f));

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Loaded properties: " + p);
        }
        return p;
    }

    /**
     * Obtain reference to default application properties. The file must exist
     * at the base of the classpath.
     *
     * @param fileName
     *                     name for the context properties file
     * @return reference to default properties file
     */
    private static InputStream defaultPropertiesFile(final String fileName) {
        return Mediator.require(() -> {
            return Context.class.getResourceAsStream("/" + fileName);
        });
    }

    /**
     * Obtain reference to run directory properties. Will check for the optional
     * file in the current user directory.
     *
     * @param fileName
     *                     name for the context properties file
     * @return reference to the run directory properties file
     */
    private static Optional<InputStream> rundirPropertiesFile(
            final String fileName) {
        return Mediator.attempt(() -> {
            return Files.newInputStream(Paths
                    .get(System.getProperty("user.dir")).resolve(fileName));
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
}
