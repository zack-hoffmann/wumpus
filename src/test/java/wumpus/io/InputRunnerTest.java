package wumpus.io;

import static org.junit.Assert.assertFalse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

/**
 * Tests of the input runner.
 */
public final class InputRunnerTest {

    /**
     * Time to wait for thread to stop.
     */
    private static final int WAIT = 1000;

    /**
     * Validate that a runner with no input can be stopped.
     *
     * @throws IOException
     *                                  if there is a problem reading from the
     *                                  stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void runsWithNoInput() throws IOException, InterruptedException {
        final Queue<String> q = new LinkedList<>();
        final InputRunner ir = new InputRunner(
                new ByteArrayInputStream("\n".getBytes()), q);
        final Thread t = new Thread(ir);
        synchronized (ir) {
            t.start();
            ir.wait();
        }
        ir.stop();
        t.join(WAIT);
        assertFalse(t.isAlive());
    }
}
