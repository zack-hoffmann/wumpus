package wumpus.engine.command;

import java.util.Set;

import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Listener;

/**
 * Command for quiting the game.
 */
public final class Quit implements Command {

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        store.get(source).ifPresent(e -> e.deregisterComponent(Listener.class));
        return "Leaving the game...";
    }

    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public Set<String> getAliases() {
        return Set.of("logout");
    }

}
