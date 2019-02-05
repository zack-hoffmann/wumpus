package wumpus.engine.entity.component;

import java.util.function.Consumer;

/**
 * Entity is able to receive messages.
 */
public final class Listener extends AbstractEntityComponent
        implements TransientComponent {

    /**
     * Operation to be performed on message receipt.
     */
    private final Consumer<Object> out;

    /**
     * Constructs a listener with a consuming operation for when it is told a
     * message.
     *
     * @param o
     *              the consuming operation
     */
    public Listener(final Consumer<Object> o) {
        this.out = o;
    }

    /**
     * Communicate to the entity.
     *
     * @param o
     *              the message for the entity
     */
    public void tell(final Object o) {
        out.accept(o);
    }

}
