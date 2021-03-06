package wumpus.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runnable for pulling lines from a queue and printing them to an output
 * stream.
 */
public final class OutputRunner implements Runnable {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger
            .getLogger(OutputRunner.class.getName());

    /**
     * The output stream to write to.
     */
    private final OutputStream out;

    /**
     * The queue to poll from.
     */
    private final Queue<String> queue;

    /**
     * Controls running continuation.
     */
    private boolean run;

    /**
     * Construct an output runner with the given stream and queue.
     *
     * @param o
     *              the output stream to write to
     * @param q
     *              the queue to poll from
     */
    public OutputRunner(final OutputStream o, final Queue<String> q) {
        this.out = o;
        this.queue = q;
        run = false;
    }

    @Override
    public void run() {
        synchronized (this) {
            run = true;
            this.notifyAll();
        }
        String line;
        try (final PrintStream ps = new PrintStream(out, false,
                Charset.defaultCharset())) {
            while (isRunning()) {
                line = queue.poll();
                if (line != null) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Output found: " + line);
                    }
                    ps.print(line);
                    ps.flush();
                }
            }
        } finally {
            synchronized (this) {
                run = false;
                this.notifyAll();
            }
        }
    }

    /**
     * Call to halt running.
     *
     * @throws IOException
     *                         when there was an issue with closing the input
     *                         stream
     */
    public synchronized void stop() throws IOException {
        run = false;
        out.close();
    }

    /**
     * Determines whether or not it is running.
     *
     * @return true if running
     */
    public synchronized boolean isRunning() {
        return run;
    }
}
