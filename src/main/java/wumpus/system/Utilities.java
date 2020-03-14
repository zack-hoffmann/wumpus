package wumpus.system;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import wumpus.Mediator;

public interface Utilities {

    String PROP_FILE = "wumpus.properties";

    Supplier<InputStream> openClasspathStream = () -> Utilities.class
            .getResourceAsStream("/" + PROP_FILE);

    Supplier<InputStream> openLocalStream = () -> Mediator
            .require(() -> Files.newInputStream(Paths
                    .get(System.getProperty("user.dir")).resolve(PROP_FILE)));

    BiFunction<Properties, InputStream, Properties> loadPropertiesFromStream = (
            p, i) -> {
        Mediator.require(p::load, i);
        return p;
    };

    UnaryOperator<Properties> loadDefaultProperties = p -> loadPropertiesFromStream
            .apply(p, openClasspathStream.get());

    UnaryOperator<Properties> loadLocalProperties = p -> loadPropertiesFromStream
            .apply(p, openLocalStream.get());

    UnaryOperator<Properties> loadProperties = p -> loadDefaultProperties
            .andThen(loadLocalProperties).apply(p);
}