package wumpus.external;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import wumpus.Mediator;

@FunctionalInterface
public interface Properties {

    class Jup extends java.util.Properties {
        private static final long serialVersionUID = -2960249146874258086L;
    }

    String PROP_FILE = "wumpus.properties";

    Supplier<InputStream> openClasspathStream = () -> Properties.class
            .getResourceAsStream("/" + PROP_FILE);

    Supplier<InputStream> openLocalStream = () -> Mediator
            .require(() -> Files.newInputStream(Paths
                    .get(System.getProperty("user.dir")).resolve(PROP_FILE)));

    BiFunction<Jup, InputStream, Jup> loadPropertiesFromStream = (p, i) -> {
        Mediator.require(p::load, i);
        return p;
    };

    UnaryOperator<Jup> loadDefaultProperties = p -> loadPropertiesFromStream
            .apply(p, openClasspathStream.get());

    UnaryOperator<Jup> loadLocalProperties = p -> loadPropertiesFromStream
            .apply(p, openLocalStream.get());

    Supplier<Properties> create = () -> {
        final java.util.Properties p = loadDefaultProperties
                .andThen(loadLocalProperties).apply(new Jup());
        return s -> p.getProperty(s);
    };

    String property(final String p);
}