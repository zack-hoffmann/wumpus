package wumpus.engine.command;

import java.util.Set;

import wumpus.engine.entity.EntityStore;

/**
 * Parent interface for all game commands.
 */
public interface Command {

    /**
     * Execute the command.
     *
     * @param source
     *                   the ID of the player entity submitting the command
     * @param store
     *                   the entity store
     * @param args
     *                   arguments to the command
     * @return command response
     */
    String exec(final long source, final EntityStore store,
            final String... args);

    /**
     * The name of the command.
     *
     * @return the name of the command
     */
    String name();

    /**
     * The aliases of the command.
     *
     * @return aliases of the command
     */
    Set<String> aliases();

}
