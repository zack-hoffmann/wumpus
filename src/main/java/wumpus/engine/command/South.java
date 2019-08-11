package wumpus.engine.command;

import java.util.Set;

import wumpus.engine.entity.EntityStore;

/**
 * Shortcut for "move south".
 */
public final class South implements Command {

    /**
     * Wrapped move command.
     */
    private final Move mover = new Move();

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        return mover.exec(source, store, "south");
    }

    @Override
    public String name() {
        return "south";
    }

    @Override
    public Set<String> aliases() {
        return Set.of("s");
    }

}
