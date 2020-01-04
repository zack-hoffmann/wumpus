package wumpus;

import java.util.Queue;

import org.eclipse.jetty.websocket.api.Session;

@FunctionalInterface
public interface Gateway {

    enum Direction {
        INBOUND, OUTBOUND;
    }

    static Gateway create(final App app, final Queue<Message> queue) {
        final Gateway gw = (m, s, d) -> {
            switch (d) {
            case INBOUND:
                acceptInbound(app, m, s, queue);
                break;
            case OUTBOUND:
                sendToRemote(app, m);
            }
        };

        return gw;
    }

    static void acceptInbound(final App app, final Message m, final Session s,
            final Queue<Message> queue) {
        switch (m.type()) {
        case TOKEN:
            processTokenMessage(app, m.token(), s);
            break;
        case RESPONSE:
            // TODO login process
            break;
        default:
            queue.add(m);
        }
    }

    static void processTokenMessage(final App app, final String token,
            final Session s) {

        if (app.initialSessionPool().session(token).isPresent()) {
            // TODO login process
        } else {
            String newToken = null;
            if ("".equals(token) && s != null) {
                newToken = app.initialSessionPool().register(s);
            } else if (app.characterSessionPool().session(token).isPresent()) {
                newToken = app.characterSessionPool().renew(token);
            } else if (app.playerSessionPool().session(token).isPresent()) {
                newToken = app.playerSessionPool().renew(token);
            } else {
                // TODO exception?
            }
            final Message newMessage = Message.Type.TOKEN.newMessage(app,
                    newToken);
            sendToRemote(app, newMessage);
        }

    }

    static void sendToRemote(final App app, final Message msg) {

        Mediator.attempt(t -> app.characterSessionPool().session(msg.token())
                .or(() -> app.playerSessionPool().session(msg.token()))
                .or(() -> app.initialSessionPool().session(msg.token())).get()
                .getRemote().sendString(t), msg.rawString());
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
}