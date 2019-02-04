package wumpus.io;

import java.io.IOException;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * An IO adapter using standard input/output.
 */
public final class StandardIOAdapter implements IOAdapter {

    /**
     * The executor service for the runner threads.
     */
    private final ExecutorService serv;

    /**
     * The input runner for this IO.
     */
    private final InputRunner inRunner;

    /**
     * The output runner for this IO.
     */
    private final OutputRunner outRunner;

    /**
     * Input queue.
     */
    private final Queue<String> inQueue;

    /**
     * Output queue.
     */
    private final Queue<String> outQueue;

    /**
     * Construct and start the adapter with the provided service.
     *
     * @param s
     *              an executor service to run the runner, supporting at least
     *              two threads
     */
    public StandardIOAdapter(final ExecutorService s) {
        serv = s;
        inQueue = new ConcurrentLinkedQueue<>();
        outQueue = new ConcurrentLinkedQueue<>();
        inRunner = new InputRunner(System.in, inQueue);
        outRunner = new OutputRunner(System.out, outQueue);
        serv.execute(inRunner);
        serv.execute(outRunner);
    }

    @Override
    public Optional<String> poll() {
        return Optional.ofNullable(inQueue.poll());
    }

    @Override
    public void post(final String o) {
        outQueue.add(o);
    }

    @Override
    public void shutdown() throws IOException {
        inRunner.stop();
        outRunner.stop();
    }
}
