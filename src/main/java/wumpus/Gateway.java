package wumpus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;

@FunctionalInterface
public interface Gateway {

    enum Direction {
        INBOUND, OUTBOUND;
    }

    // TODO don't like static state...
    Map<String, Session> INITIAL_SESSIONS = new HashMap<String, Session>();
    Map<String, Session> PLAYER_SESSIONS = new HashMap<String, Session>();
    Map<String, Session> CHARACTER_SESSIONS = new HashMap<String, Session>();

    static Gateway create(final Context ctx) {
        final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();

        final Gateway gw = (m, s, d) -> {
            switch (d) {
            case INBOUND:
                acceptInbound(m, s);
                break;
            case OUTBOUND:
                sendToRemote(m);
            }
            if (Direction.INBOUND.equals(d)) {
                if (Message.Type.TOKEN.equals(m.type())) {
                    if (s != null) {
                        sendToRemote(Message.Type.TOKEN.newMessage(ctx,
                                registerInitialSession(ctx, s)));
                    } else {
                        // TODO ???
                    }
                } else {
                    // TODO handle logins
                    // TODO queue it
                }
            } else {
                sendToRemote(m);
            }

        };

        return gw;
    }

    static void acceptInbound(final Message m, final Session s) {
        switch (m.type()) {
        case TOKEN:
            processTokenMessage(m.token(), s);
            break;
        }
    }

    static void processTokenMessage(final String token, final Session s) {
        if ("".equals(token) && s != null) {
            // TODO initial session
        } else {
            // TODO figure out which session pool, generate new token, replace
            // value, send it back...
        }

    }

    static Optional<Session> resolveTokenToSession(final String token) {
        return Optional.ofNullable(
                CHARACTER_SESSIONS.getOrDefault(token, PLAYER_SESSIONS
                        .getOrDefault(token, INITIAL_SESSIONS.get(token))));
    }

    static void sendToRemote(final Message msg) {
        Mediator.attempt(t -> resolveTokenToSession(msg.token()).get()
                .getRemote().sendString(t), msg.rawString());
    }

    static String registerInitialSession(final Context ctx, final Session s) {
        final String t = StringPool.tokenPool(ctx).newToken();
        INITIAL_SESSIONS.put(t, s);
        return t;
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