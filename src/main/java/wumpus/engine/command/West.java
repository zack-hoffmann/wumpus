package wumpus.engine.command;

import java.util.Set;

import wumpus.engine.entity.EntityStore;

/**
 * Shortcut for "move west".
 */
public final class West implements Command {

    /**
     * Wrapped move command.
     */
    private final Move mover = new Move();

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        return mover.exec(source, store, "west");
    }

    @Override
    public String name() {
        return "west";
    }

    @Override
    public Set<String> aliases() {
        return Set.of("w");
    }

}
