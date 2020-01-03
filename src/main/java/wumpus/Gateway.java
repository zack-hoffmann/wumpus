package wumpus;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;

@FunctionalInterface
public interface Gateway {

    enum Direction {
        INBOUND, OUTBOUND;
    }

    static Gateway create(final App app) {
        final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();

        final Gateway gw = (m, s, d) -> {
            switch (d) {
            case INBOUND:
                acceptInbound(app, m, s);
                break;
            case OUTBOUND:
                sendToRemote(app, m);
            }
        };

        return gw;
    }

    static void acceptInbound(final App app, final Message m, final Session s) {
        switch (m.type()) {
        case TOKEN:
            processTokenMessage(app, m.token(), s);
            break;
        }
    }

    static void processTokenMessage(final App app, final String token,
            final Session s) {
        if ("".equals(token) && s != null) {
            final String newToken = app.initialSessionPool().register(s);
            final Message newMessage = Message.Type.TOKEN.newMessage(app,
                    newToken);
            sendToRemote(app, newMessage);
        } else {
            // TODO figure out which session pool, generate new token, replace
            // value, send it back...
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