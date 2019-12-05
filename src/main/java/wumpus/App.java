package wumpus;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application instance for the wumpus game.
 */
public final class App {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(App.class.getName());

    /**
     * Application properties file name.
     */
    private static final String PROP_FILE_NAME = "wumpus.properties";

    /**
     * Runs a new wumpus App instance using standard output.
     *
     * @param args
     *                 Command line arguments. None specified at this time.
     */
    public static void main(final String... args) {
        LOG.info("Starting application server for Hunt the Wumpus");
        LOG.fine("Fine logging is enabled.");

        final Context ctx = Context.create(App::loadProperties);
        ctx.property("hello");
    }

    /**
     * Obtain reference to default application properties. The file must exist
     * at the base of the classpath.
     *
     * @return reference to default properties file
     */
    private static File defaultPropertiesFile() {
        return Mediator.require(() -> {
            return Paths.get(App.class.getResource(PROP_FILE_NAME).toURI())
                    .toFile();
        });
    }

    /**
     * Obtain reference to run directory properties. Will check for the optional
     * file in the current user directory.
     *
     * @return reference to the run directory properties file
     */
    private static Optional<File> rundirPropertiesFile() {
        return Optional
                .ofNullable(Paths.get(System.getProperty("user.dir"))
                        .resolve(PROP_FILE_NAME).toFile())
                .filter(f -> f.exists());
    }

    /**
     * Attempts to load a properties file in to the properties object.
     *
     * @param p
     *              the properties object
     * @param f
     *              the file reference
     */
    private static void loadFileToProperties(final Properties p, final File f) {
        LOG.fine(() -> String.format("Properties found at '%s'.",
                f.getAbsolutePath()));

        Mediator.attempt(fp -> {
            try (final InputStream is = Files.newInputStream(fp)) {
                p.load(is);
            }
        }, f.toPath());

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
     * Private constructor.
     */
    private App() {
        LOG.warning("This class should not be initialized.");
    }
}
