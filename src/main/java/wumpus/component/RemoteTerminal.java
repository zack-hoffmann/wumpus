package wumpus.component;

import java.util.function.Function;

@FunctionalInterface
public interface RemoteTerminal
        extends TransientComponent<Function<Message, Boolean>> {

    default Boolean send(final Message m) {
        return value().apply(m);
    }

    default boolean isOpen() {
        return send(Message.Type.PING.newMessage());
    }
}