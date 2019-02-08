package wumpus.engine.command;

import java.util.Set;

/**
 * Parent interface for all game commands.
 */
public interface Command {

    /**
     * Execute the command.
     *
     * @param args
     *                 arguments to the command
     * @return command response
     */
    String exec(final String... args);

    /**
     * The name of the command.
     *
     * @return the name of the command
     */
    String getName();

    /**
     * The aliases of the command.
     *
     * @return aliases of the command
     */
    Set<String> getAliases();

}
