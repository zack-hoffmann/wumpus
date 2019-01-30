package wumpus.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
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
     * Queue for testing.
     */
    private Queue<String> tempQueue;

    /**
     * Executor service to run input runners.
     */
    private ExecutorService serv;

    /**
     * Reset the temp queue and executor service.
     */
    @Before
    public void initialize() {
        tempQueue = new LinkedList<>();
        serv = Executors.newSingleThreadExecutor();
    }

    /**
     * Kill hanging threads.
     */
    @After
    public void killThreads() {
        serv.shutdownNow();
    }

    /**
     * Validate that a runner with no input can be stopped.
     *
     * @throws IOException
     *                                  if there is a problem reading the stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void runsWithNoInput() throws IOException, InterruptedException {
        final InputRunner ir = new InputRunner(streamFromString(""), tempQueue);
        serv.execute(ir);
        synchronized (ir) {
            ir.wait();
        }
        ir.stop();
        serv.awaitTermination(1, TimeUnit.SECONDS);
        serv.shutdown();
        assertFalse(ir.isRunning());
        assertTrue(serv.isShutdown());
    }

    /**
     * Validate that a runner with no input queues no input.
     *
     * @throws IOException
     *                                  if there is a problem reading the stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void queuesNoInput() throws IOException, InterruptedException {
        final InputRunner ir = new InputRunner(streamFromString(""), tempQueue);
        serv.execute(ir);
        synchronized (ir) {
            ir.wait();
        }
        ir.stop();
        serv.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(tempQueue.isEmpty());
    }

    /**
     * Validates that a runner queues input.
     *
     * @throws IOException
     *                                  if there is a problem reading the stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void queuesInput() throws IOException, InterruptedException {
        final InputRunner ir = new InputRunner(streamFromString("test"),
                tempQueue);
        serv.execute(ir);
        synchronized (ir) {
            ir.wait();
        }
        Thread.sleep(1);
        ir.stop();
        serv.awaitTermination(1, TimeUnit.SECONDS);
        assertEquals(1, tempQueue.size());
    }

    /**
     * Validates that a runner queues multiple inputs.
     *
     * @throws IOException
     *                                  if there is a problem reading the stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void queuesMultipleInput() throws IOException, InterruptedException {
        final InputRunner ir = new InputRunner(streamFromString("test\ntest"),
                tempQueue);
        serv.execute(ir);
        synchronized (ir) {
            ir.wait();
        }
        Thread.sleep(1);
        ir.stop();
        serv.awaitTermination(1, TimeUnit.SECONDS);
        assertEquals(2, tempQueue.size());
    }
}
