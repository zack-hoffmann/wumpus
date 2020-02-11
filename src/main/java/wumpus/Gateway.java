package wumpus;

import java.util.Optional;
import java.util.Queue;

@FunctionalInterface
public interface Gateway {

    enum Direction {
        INBOUND, OUTBOUND;
    }

    static Gateway create(final App app, final Queue<Message> queue) {
        return (m, s, d) -> {
            switch (d) {
            case INBOUND:
                acceptInbound(app, m, s, queue);
                break;
            case OUTBOUND:
                sendToRemote(app, m, m.token());
            }
        };
    }

    static void acceptInbound(final App app, final Message m, final Session s,
            final Queue<Message> queue) {
        if (m.token().isBlank() && !m.type().equals(Message.Type.TOKEN)) {
            s.send(Message.Type.ERROR.newMessage(app,
                    "Illegal message: No token present. Please send an initial token reqest first.")
                    .rawString());
        } else {
            switch (m.type()) {
            case TOKEN:
                processTokenMessage(app, m.token(), s);
                break;
            case LOGIN:
                handleLogin(app, m.token(), m.params()[0], m.params()[1]);
                break;
            default:
                if (app.authenticator().status(
                        m.token()) == Authenticator.Status.AUTHENTICATED) {
                    queue.add(m);
                }
            }
        }
    }

    static void processTokenMessage(final App app, final String token,
            final Session s) {

        String newToken = "";
        if ("".equals(token) && s != null) {
            newToken = app.initialSessionPool().register(s);
        } else if (app.initialSessionPool().session(token).isPresent()) {
            newToken = app.initialSessionPool().renew(token).orElse("");
        } else if (app.characterSessionPool().session(token).isPresent()) {
            newToken = app.characterSessionPool().renew(token).orElse("");
        } else if (app.playerSessionPool().session(token).isPresent()) {
            newToken = app.playerSessionPool().renew(token).orElse("");
        } else {
            newToken = "";
        }
        final Message newMessage = Message.Type.TOKEN.newMessage(app, newToken);
        sendToRemote(app, newMessage, newToken);

    }

    static void handleLogin(final App app, final String token,
            final String username, final String password) {
        Message msg;
        String outToken = token;
        switch (app.authenticator().authenticate(token, username, password)) {
        case AUTHENTICATED:
            final Optional<String> newToken = app.initialSessionPool()
                    .session(token)
                    .map(s -> app.playerSessionPool().register(s));
            if (newToken.isPresent()) {
                outToken = newToken.get();
            }
            msg = Message.Type.TOKEN.newMessage(app, outToken);

            break;
        // TODO other statuses
        default:
            msg = Message.Type.ERROR.newMessage(app, "Not Authenticated.");
        }
        sendToRemote(app, msg, outToken);
    }

    static void sendToRemote(final App app, final Message msg,
            final String outToken) {
        Mediator.attempt(t -> app.characterSessionPool().session(outToken)
                .or(() -> app.playerSessionPool().session(outToken))
                .or(() -> app.initialSessionPool().session(outToken)).get()
                .send(t), msg.rawString());
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
    void accept(final Message message, final Session session,
            final Direction direction);

    default void acceptInbound(final Message message, final Session session) {
        accept(message, session, Direction.INBOUND);
    }

    default void sendOutbound(final Message message) {
        accept(message, null, Direction.OUTBOUND);
    }
}