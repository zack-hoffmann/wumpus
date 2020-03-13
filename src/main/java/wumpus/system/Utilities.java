package wumpus.system;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import wumpus.Mediator;

public interface Utilities {

    String PROP_FILE = "wumpus.properties";

    Supplier<InputStream> openClasspathStream = () -> Utilities.class
            .getResourceAsStream("/" + PROP_FILE);

    Supplier<InputStream> openLocalStream = () -> Mediator
            .require(() -> Files.newInputStream(Paths
                    .get(System.getProperty("user.dir")).resolve(PROP_FILE)));

    BiFunction<Properties, Supplier<InputStream>, Properties> loadPropertiesByStream = (
            p, i) -> {
        Mediator.require(p::load, i.get());
        return p;
    };

    Function<Properties, Properties> loadDefaultProperties = p -> loadPropertiesByStream
            .apply(p, openClasspathStream);

    Function<Properties, Properties> loadLocalProperties = p -> loadPropertiesByStream
            .apply(p, openLocalStream);

    Function<Properties, Properties> loadProperties = loadDefaultProperties
            .andThen(loadLocalProperties);
}