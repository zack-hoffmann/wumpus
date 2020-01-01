package wumpus;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;

@FunctionalInterface
public interface Gateway {

    enum Direction {
        INBOUND, OUTBOUND;
    }

    static Gateway create(final Context ctx) {
        final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();

        final Gateway gw = (m, s, d) -> {
            switch (d) {
            case INBOUND:
                acceptInbound(ctx, m, s);
                break;
            case OUTBOUND:
                sendToRemote(ctx, m);
            }
        };

        return gw;
    }

    static void acceptInbound(final Context ctx, final Message m,
            final Session s) {
        switch (m.type()) {
        case TOKEN:
            processTokenMessage(ctx, m.token(), s);
            break;
        }
    }

    static void processTokenMessage(final Context ctx, final String token,
            final Session s) {
        if ("".equals(token) && s != null) {
            final String newToken = SessionPool.initialPool(ctx).register(s,
                    ctx);
            final Message newMessage = Message.Type.TOKEN.newMessage(ctx,
                    newToken);
            sendToRemote(ctx, newMessage);
        } else {
            // TODO figure out which session pool, generate new token, replace
            // value, send it back...
        }

    }

    static void sendToRemote(final Context ctx, final Message msg) {
        Mediator.attempt(
                t -> SessionPool.resolveTokenToSession(ctx, msg.token()).get()
                        .getRemote().sendString(t),
                msg.rawString());
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