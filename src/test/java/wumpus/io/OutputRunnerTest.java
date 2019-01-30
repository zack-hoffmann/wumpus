package wumpus.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
}
