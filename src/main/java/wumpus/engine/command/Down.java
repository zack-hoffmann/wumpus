package wumpus.engine.command;

import java.util.Set;

import wumpus.engine.entity.EntityStore;

/**
 * Shortcut for "move down".
 */
public final class Down implements Command {

    /**
     * Wrapped move command.
     */
    private final Move mover = new Move();

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        return mover.exec(source, store, "down");
    }

    @Override
    public String getName() {
        return "down";
    }

    @Override
    public Set<String> getAliases() {
        return Set.of("d");
    }

}
