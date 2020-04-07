package wumpus.component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import wumpus.Token;
import wumpus.external.StringPool;

/**
 * Wrapper for exchange of string parts between sender and receiver, typically
 * client and server endpoints.
 */
@FunctionalInterface
public interface Message extends ValueComponent<String[]> {

    /**
     * Delimiter for message parts.
     */
    String DELIM = new String(new char[] {(char) 31});

    /**
     * Fixed set of message types available.
     */
    enum Type {
        /**
         * Perform no action other than to test validity of the connection.
         */
        PING,
        /**
         * If no parameters are given, the sender is requesting a new
         * identifying token for itself from the recipient (a "renew"). If a
         * parameter is given, the sender is indicating that is the token which
         * should be used for the recipient to communicate with it.
         */
        TOKEN,
        /**
         * If no parameters are given, the sender is indicating that it is
         * waiting for the recipient to send it a login request. If one
         * parameter is given it must contain a single-use login token issued to
         * the sender. If two parameters are given they must be the sender's
         * username and password, in that order.
         */
        LOGIN,
        /**
         * The sender is requesting that the recipient execute a command. Mostly
         * used for the client to send game commands to the server or for the
         * server to send display commands to the client.
         */
        COMMAND,
        /**
         * The sender is notifying the receiver that something has gone wrong
         * which may impact their operation. The first parameter will be a
         * number from 1 to 5 indicating severity.<br />
         * 1 - A trivial error has occurred and nothing needs to be done in
         * response.<br />
         * 2 - Some activity the recipient was attempting failed due to a
         * mistake (such as user error).<br />
         * 3 - Some activity the recipient was attempting failed or was
         * disrupted and should be reattempted.<br />
         * 4 - The connection has been impaired and the recipient should attempt
         * to reconnect.<br />
         * 5 - The operation of the sender has been impaired and the sender may
         * not be available for reconnection.<br />
         * <br />
         * The second parameter will be a brief description of the error.
         */
        ERROR;

        /**
         * String names of all valid types. Used for validation.
         */
        public static final Set<String> TYPE_SET = Arrays.stream(Type.values())
                .map(Type::toString).collect(Collectors.toUnmodifiableSet());

        /**
         * Create a new message of this type.
         *
         * @param app
         *                   the application instance, used for string interning
         * @param token
         *                   the message sender identity token
         * @param params
         *                   any parameters needed for the message
         * @return the new message
         */
        public Message newMessage(final Token token, final String... params) {
            return Message.fromParts(token, this, params);
        }

        /**
         * Create a new message of this type with no token.
         *
         * @param app
         *                   the application instance, used for string interning
         * @param params
         *                   any parameters needed for the message
         * @return the new message
         */
        public Message newMessage(final String... params) {
            return Message.fromParts(Token.none.get(), this, params);
        }
    }

    /**
     * Exception generated by the message parsing process.
     */
    class ParseException extends RuntimeException {

        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Message is invalid because there are no delimited parts.
         *
         * @return a new parse exception
         */
        private static ParseException delimiterNotFound() {
            return new ParseException("Delimiter not found.");
        }

        /**
         * Message is invalid because the type is invalid.
         *
         * @param type
         *                 the invalid message type being attempted
         * @return a new parse exception
         */
        private static ParseException unrecognizedMessageType(
                final String type) {
            return new ParseException(
                    String.format("Unrecognized message type '%s'.", type));
        }

        /**
         * Message is invalid because the required parts are not present.
         *
         * @return a new parse exception
         */
        private static ParseException insufficientParts() {
            return new ParseException("Not enough message parts. "
                    + "All messages require at least a token (may be empty) "
                    + "and a message type.");
        }

        /**
         * Internal constructor. Calls the parent.
         *
         * @param message
         *                    the exception message
         */
        private ParseException(final String message) {
            super(message);
        }

    }

    /**
     * Create a new message.
     *
     * @param app
     *                   the application instance, used for string interning
     * @param token
     *                   the message sender identity token
     * @param type
     *                   the type of the message
     * @param params
     *                   any parameters needed for the message
     * @return the new message
     */
    static Message fromParts(final Token token, final Type type,
            final String... params) {
        final String[] parts = new String[params.length + 2];
        parts[0] = token.string();
        parts[1] = type.toString();
        for (int i = 0; i < params.length; i++) {
            parts[i + 2] = StringPool.parameterPool.intern(params[i]);
        }
        return () -> parts;
    }

    /**
     * Parse a raw message in to message format.
     *
     * @param app
     *                the application instance, used for string interning
     * @param raw
     *                the raw string message
     * @return the newly parsed message
     */
    static Message parse(final String raw) {
        final String[] parts = raw.split(DELIM);
        if (parts.length < 1) {
            throw ParseException.delimiterNotFound();
        }
        if (parts.length < 2) {
            throw ParseException.insufficientParts();
        }
        if (!Type.TYPE_SET.contains(parts[1])) {
            throw ParseException.unrecognizedMessageType(parts[1]);
        }
        final Type t = Type.valueOf(parts[1]);
        final String[] params = new String[parts.length - 2];
        for (int i = 0; i < params.length; i++) {
            params[i] = parts[i + 2];
        }
        return fromParts(Token.of.apply(parts[0]), t, params);
    }

    /**
     * The type of the message.
     *
     * @return the type of the message
     */
    default Type type() {
        return Type.valueOf(value()[1]);
    }

    /**
     * The message token.
     *
     * @return the message token or empty string if not used
     */
    default String token() {
        return value()[0];
    }

    /**
     * The message params.
     *
     * @return the message params
     */
    default String[] params() {
        final String[] parts = value();
        final String[] params = new String[parts.length - 2];
        for (int i = 0; i < params.length; i++) {
            params[i] = parts[i + 2];
        }
        return params;
    }

    /**
     * Converts parts to whole raw string, usually for transmission. Interface
     * types cannot override toString, or else that would be used instead.
     *
     * @return the raw string representation of the message
     */
    default String rawString() {
        return Arrays.stream(value()).collect(Collectors.joining(DELIM));
    }
}
