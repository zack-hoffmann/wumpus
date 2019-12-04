package wumpus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Application instance for the wumpus game.
 */
public class App {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(App.class.getName());

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
    }

    private static File rundirPropertiesFile() {
        return Paths.get(System.getProperty("user.dir"))
                .resolve("wumpus.properties").toFile();
    }

    private static void loadFileToProperties(final Properties p, final Supplier<File> fs) {
        Optional.of(fs.get()).filter(File::exists).ifPresent(f -> {
            LOG.fine(() -> String.format(
                    "Run directory properties found at '%s'.",
                    f.getAbsolutePath()));

            try {
                p.load(Files.newInputStream(f.toPath()));
            } catch (IOException e) {
                LOG.log(Level.WARNING, e,
                        () -> String.format(
                                "Could not load properties from '%s'.",
                                f.getAbsolutePath()));
            }
        });  
    }

    private static Map<String, String> loadProperties() {
        LOG.info("Loading properties...");

        final Properties rundirProperties = new Properties();
        loadFileToProperties(rundirProperties, App::rundirPropertiesFile);

        return rundirProperties.entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey().toString(), e -> e.getValue().toString()));
    }

}
