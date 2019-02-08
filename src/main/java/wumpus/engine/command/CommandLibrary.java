package wumpus.engine.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Master for all game commands, for easy execution.
 */
public final class CommandLibrary {

    /**
     * Bogus command to reference when a bad command name is given.
     */
    private static final class InvalidCommand implements Command {

        @Override
        public String exec(final String... args) {
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
     * Master array of commands.
     */
    private static final Command[] COMMANDS = {};

    /**
     * Map of command names to commands.
     */
    private static final Map<String, Command> NAME_MAP = Arrays.stream(COMMANDS)
            .collect(Collectors.toMap(c -> c.getName(), Function.identity()));

    /**
     * Map of command aliases to commands.
     */
    private static final Map<String, Command> ALIAS_MAP = Arrays
            .stream(COMMANDS)
            .map(c -> c.getAliases().stream()
                    .collect(Collectors.toMap(Function.identity(), s -> c)))
            .map(m -> m.entrySet()).flatMap(s -> s.stream())
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

    /**
     * Execute a command by name or alias.
     *
     * @param command
     *                    the name or alias of the command
     * @param args
     *                    arguments for the command
     * @return the command response
     */
    public String execute(final String command, final String... args) {
        return NAME_MAP
                .getOrDefault(command, ALIAS_MAP.getOrDefault(command, INVALID))
                .exec(args);
    }
}
