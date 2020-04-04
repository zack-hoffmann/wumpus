package wumpus.component;

import java.util.function.Consumer;

@FunctionalInterface
public interface RemoteTerminal
        extends TransientComponent<Consumer<Message>> {

    default void send(final Message m) {
        value().accept(m);
    }

}