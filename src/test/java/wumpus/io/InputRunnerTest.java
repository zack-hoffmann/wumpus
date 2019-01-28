package wumpus.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

/**
 * Tests of the input runner.
 */
public final class InputRunnerTest {

    /**
     * Generate an input stream from a string.
     *
     * @param s
     *              the string that the input stream should wrap
     * @return the wrapping input stream
     */
    private static InputStream streamFromString(final String s) {
        return new ByteArrayInputStream(s.getBytes());
    }

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
        final InputRunner ir = new InputRunner(streamFromString(""), q);
        final Thread t = new Thread(ir);
        synchronized (ir) {
            t.start();
            ir.wait();
        }
        ir.stop();
        t.join(WAIT);
        assertFalse(t.isAlive());
    }

    /**
     * Validate that a runner with no input queues no input.
     *
     * @throws IOException
     *                                  if there is a problem reading from the
     *                                  stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void queuesNoInput() throws IOException, InterruptedException {
        final Queue<String> q = new LinkedList<>();
        final InputRunner ir = new InputRunner(streamFromString(""), q);
        final Thread t = new Thread(ir);
        synchronized (ir) {
            t.start();
            ir.wait();
        }
        ir.stop();
        t.join(WAIT);
        assertTrue(q.isEmpty());
    }
}
