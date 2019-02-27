package wumpus.engine.command;

import java.util.Set;
import java.util.stream.Collectors;

import wumpus.engine.entity.EntityStore;

/**
 * Command to dump current entity states.
 */
public final class Debug implements Command {

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        return store.stream()
                .map(e -> e.getId() + "["
                        + (e.getComponents().stream()
                                .map(c -> c.getClass().getSimpleName())
                                .collect(Collectors.joining(",")))
                        + "]")
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public Set<String> getAliases() {
        return Set.of();
    }

}
