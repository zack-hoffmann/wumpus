package wumpus.component;

import java.util.function.Consumer;

@FunctionalInterface
public interface RemoteTerminal
        extends ValueComponent<Consumer<Message>>, TransientComponent {

    default void send(final Message m) {
        value().accept(m);
    }

}