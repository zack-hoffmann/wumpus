package wumpus.engine.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import wumpus.engine.entity.EntityStore;

/**
 * Master for all game commands, for easy execution.
 */
public final class CommandLibrary {

    /**
     * Bogus command to reference when a bad command name is given.
     */
    private static final class InvalidCommand implements Command {

        @Override
        public String exec(final long source, final EntityStore store,
                final String... args) {
            return "Invalid command.";
        }

        @Override
        public String getName() {
            return "noop";
        }

        @Override
        public Set<String> getAliases() {
            return new HashSet<>();
        }

    }

    /**
     * Default instance of invalid command.
     */
    private static final Command INVALID = new InvalidCommand();

    /**
     * Map of command names to commands.
     */
    private final Map<String, Command> nameMap;

    /**
     * Map of command aliases to commands.
     */
    private final Map<String, Command> aliasMap;

    /**
     * Initialize the command library with a list of commands.
     *
     * @param commands
     *                     commands to initialize the library with
     */
    public CommandLibrary(final Command... commands) {
        this.nameMap = Arrays.stream(commands).collect(
                Collectors.toMap(c -> c.getName(), Function.identity()));
        this.aliasMap = Arrays.stream(commands)
                .map(c -> c.getAliases().stream()
                        .collect(Collectors.toMap(Function.identity(), s -> c)))
                .map(m -> m.entrySet()).flatMap(s -> s.stream())
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    /**
     * Execute a command by name or alias.
     *
     * @param command
     *                    the name or alias of the command
     * @param source
     *                    the ID of the player entity submitting the command
     * @param store
     *                    the entity store
     * @param args
     *                    arguments for the command
     * @return the command response
     */
    public String execute(final String command, final long source,
            final EntityStore store, final String... args) {
        return nameMap
                .getOrDefault(command.toLowerCase(),
                        aliasMap.getOrDefault(command.toLowerCase(), INVALID))
                .exec(source, store, args);
    }
}
