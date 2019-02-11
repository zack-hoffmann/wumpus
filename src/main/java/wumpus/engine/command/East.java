package wumpus.engine.command;

import java.util.Set;

import wumpus.engine.entity.EntityStore;

/**
 * Shortcut for "move east".
 */
public final class East implements Command {

    /**
     * Wrapped move command.
     */
    private final Move mover = new Move();

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        return mover.exec(source, store, "east");
    }

    @Override
    public String getName() {
        return "east";
    }

    @Override
    public Set<String> getAliases() {
        return Set.of("e");
    }

}
