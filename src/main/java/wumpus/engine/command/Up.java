package wumpus.engine.command;

import java.util.Set;

import wumpus.engine.entity.EntityStore;

/**
 * Shortcut for "move up".
 */
public final class Up implements Command {

    /**
     * Wrapped move command.
     */
    private final Move mover = new Move();

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        return mover.exec(source, store, "up");
    }

    @Override
    public String name() {
        return "up";
    }

    @Override
    public Set<String> aliases() {
        return Set.of("u");
    }

}
