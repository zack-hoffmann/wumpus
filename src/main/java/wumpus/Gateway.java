package wumpus;

import java.util.Optional;
import java.util.Queue;

import wumpus.component.Message;

/**
 * Processing point for all messages into or out of the server.
 */
@FunctionalInterface
public interface Gateway {

    /**
     * Indicates directionality of a message in the gateway.
     */
    enum Direction {
        /**
         * Message is moving through the gateway from a remote endpoint to the
         * server.
         */
        INBOUND,
        /**
         * Message is moving through the gateway from the server to a remote
         * endpoint.
         */
        OUTBOUND;
    }

    /**
     * Error message. {@value}
     */
    String ERROR_NEED_INITIAL_TOKEN = "Illegal message: No token present. "
            + "Please send an initial token reqest first.";

    /**
     * Create a new gateway instance.
     *
     * @param app
     *                  the application instance
     * @param queue
     *                  the queue for processed inbound messages
     * @return a new gateway
     */
    static Gateway create(final Queue<Message> queue) {
        return (m, s, d) -> {
            switch (d) {
            case INBOUND:
                acceptInbound(m, s, queue);
                break;
            case OUTBOUND:
                sendToRemote(m, m.token());
                break;
            default:
            }
        };
    }

    /**
     * Process an inbound message.
     *
     * @param app
     *                  the application instance
     * @param m
     *                  the message to process
     * @param s
     *                  the session which originated the message
     * @param queue
     *                  the queue on which to place processed non-gateway
     *                  messages
     */
    static void acceptInbound(final Message m, final Session s,
            final Queue<Message> queue) {
        if (m.token().isBlank() && !m.type().equals(Message.Type.TOKEN)) {
            s.send(Message.Type.ERROR.newMessage(ERROR_NEED_INITIAL_TOKEN)
                    .rawString());
        } else {
            switch (m.type()) {
            case TOKEN:
                processTokenMessage(m.token(), s);
                break;
            case LOGIN:
                handleLogin(m.token(), m.params()[0], m.params()[1]);
                break;
            default:
                if (Authenticator.a.status(
                        m.token()) == Authenticator.Status.AUTHENTICATED) {
                    queue.add(m);
                } else {
                    sendToRemote(
                            Message.Type.ERROR.newMessage("Not Authenticated."),
                            m.token());
                }
            }
        }
    }

    /**
     * Handle processing of a TOKEN type message.
     *
     * @param app
     *                  the application instance
     * @param token
     *                  the original token identifying the session
     * @param s
     *                  the session to issue the token for
     */
    static void processTokenMessage(final String token, final Session s) {

        String newToken = "";
        if ("".equals(token) && s != null) {
            newToken = SessionPool.initialSessionPool.register(s);
        } else if (SessionPool.initialSessionPool.session(token).isPresent()) {
            newToken = SessionPool.initialSessionPool.renew(token).orElse("");
        } else if (SessionPool.playerSessionPool.session(token).isPresent()) {
            newToken = SessionPool.playerSessionPool.renew(token).orElse("");
        } else {
            newToken = "";
        }
        final Message newMessage = Message.Type.TOKEN.newMessage(newToken);
        sendToRemote(newMessage, newToken);

    }

    /**
     * Handle processing of a LOGIN type message.
     *
     * @param app
     *                     the application instance
     * @param token
     *                     the initial token identifying the session to log in
     * @param username
     *                     the username for the login attempt
     * @param password
     *                     the password for the login attempt
     */
    static void handleLogin(final String token, final String username,
            final String password) {
        Message msg;
        String outToken = token;
        switch (Authenticator.a.authenticate(token, username, password)) {
        case AUTHENTICATED:
            final Optional<String> newToken = SessionPool.initialSessionPool
                    .session(token)
                    .map(s -> SessionPool.playerSessionPool.register(s));
            if (newToken.isPresent()) {
                outToken = newToken.get();
            }
            msg = Message.Type.TOKEN.newMessage(outToken);

            break;
        // TODO other statuses
        default:
            msg = Message.Type.ERROR.newMessage("Not Authenticated.");
        }
        sendToRemote(msg, outToken);
    }

    /**
     * Send a message to a remote endpoint.
     *
     * @param app
     *                     the application instance
     * @param msg
     *                     the message to send
     * @param outToken
     *                     the token identifying the session to send outbound to
     */
    static void sendToRemote(final Message msg, final String outToken) {
        Mediator.attempt(
                t -> SessionPool.playerSessionPool.session(outToken)
                        .or(() -> SessionPool.initialSessionPool
                                .session(outToken))
                        .get().send(t),
                msg.type().newMessage(msg.params()).rawString());
    }

    /**
     * Process a message.
     *
     * @param message
     *                      the message to process
     * @param session
     *                      the session with which the message is associated
     * @param direction
     *                      the direction of the message
     */
    void accept(Message message, Session session, Direction direction);

    /**
     * Accept and process a message from a new remote endpoint.
     *
     * @param message
     *                    the message to accept
     * @param session
     *                    the session of the new endpoint
     */
    default void acceptInbound(final Message message, final Session session) {
        accept(message, session, Direction.INBOUND);
    }

    /**
     * Accept and process a message from a known remote endpoint.
     *
     * @param message
     *                    the message to accept
     */
    default void acceptInbound(final Message message) {
        accept(message, null, Direction.INBOUND);
    }

    /**
     * Send a message outbound to the remote endpoint indicated by the message
     * token.
     *
     * @param message
     *                    the message to send
     */
    default void sendOutbound(final Message message) {
        accept(message, null, Direction.OUTBOUND);
    }
}
