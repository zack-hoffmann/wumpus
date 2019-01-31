package wumpus.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests of the output runner.
 */
public final class OutputRunnerTest {

    /**
     * Milliseconds to wait for queue processing.
     */
    private static final int WAIT = 100;

    /**
     * Generate a queue of strings.
     *
     * @param ss
     *               the strings to queue
     * @return the queue of the given strings
     */
    private static Queue<String> queueFromStrings(final String... ss) {
        return Arrays.stream(ss)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Output stream used for testing.
     */
    private ByteArrayOutputStream out;

    /**
     * Executor service to run input runners.
     */
    private ExecutorService serv;

    /**
     * Reset the temp queue and executor service.
     */
    @Before
    public void initialize() {
        out = new ByteArrayOutputStream();
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
     * Validate that a runner with no output can be stopped.
     *
     * @throws IOException
     *                                  if there is a problem reading the stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void runsWithNoOutput() throws InterruptedException, IOException {
        final OutputRunner or = new OutputRunner(out, queueFromStrings());
        serv.execute(or);
        synchronized (or) {
            or.wait();
        }
        or.stop();
        serv.awaitTermination(1, TimeUnit.SECONDS);
        serv.shutdown();
        assertFalse(or.isRunning());
        assertTrue(serv.isShutdown());
    }

    /**
     * Validate that a runner with no output writes no output.
     *
     * @throws IOException
     *                                  if there is a problem reading the stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void printsNoOutput() throws InterruptedException, IOException {
        final OutputRunner or = new OutputRunner(out, queueFromStrings());
        serv.execute(or);
        synchronized (or) {
            or.wait();
        }
        Thread.sleep(WAIT);
        or.stop();
        serv.awaitTermination(1, TimeUnit.SECONDS);
        assertEquals(0, out.toByteArray().length);
    }

    /**
     * Validate that a runner writes output.
     *
     * @throws IOException
     *                                  if there is a problem reading the stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void printsOutput() throws InterruptedException, IOException {
        final OutputRunner or = new OutputRunner(out, queueFromStrings("test"));
        serv.execute(or);
        synchronized (or) {
            or.wait();
        }
        Thread.sleep(WAIT);
        or.stop();
        serv.awaitTermination(1, TimeUnit.SECONDS);
        String o = new String(out.toByteArray(), Charset.defaultCharset())
                .trim();
        assertEquals("test", o);
    }

    /**
     * Validate that a runner writes multiple outputs.
     *
     * @throws IOException
     *                                  if there is a problem reading the stream
     * @throws InterruptedException
     *                                  if there is a problem joining the thread
     */
    @Test
    public void printsMultipleOutput()
            throws InterruptedException, IOException {
        final OutputRunner or = new OutputRunner(out,
                queueFromStrings("test", "test2"));
        serv.execute(or);
        synchronized (or) {
            or.wait();
        }
        Thread.sleep(WAIT);
        or.stop();
        serv.awaitTermination(1, TimeUnit.SECONDS);
        String[] outs = new String(out.toByteArray(), Charset.defaultCharset())
                .trim().split("\n");
        assertEquals("test", outs[0].trim());
        assertEquals("test2", outs[1].trim());
    }
}
