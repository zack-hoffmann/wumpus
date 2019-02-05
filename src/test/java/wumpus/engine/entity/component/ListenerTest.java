package wumpus.engine.entity.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for listening entities.
 */
public final class ListenerTest {

    /**
     * Tests that a consuming operation is performed.
     */
    @Test
    public void listens() {
        final Object[] os = new Object[1];
        final Listener l = new Listener(m -> {
            os[0] = m;
        });
        l.tell("test");
        assertEquals("test", os[0]);
    }
}
