package wumpus.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Queue;

/**
 * Runnable for pulling lines from an input stream and queuing them.
 */
public final class InputRunner implements Runnable {

    /**
     * The input stream to read from.
     */
    private final InputStream in;

    /**
     * The queue to post to.
     */
    private final Queue<String> queue;

    /**
     * Controls running continuation.
     */
    private boolean run;

    /**
     * Construct an input runner with the given stream and queue.
     *
     * @param i
     *              the input stream to read from
     * @param q
     *              the queue to post to
     */
    public InputRunner(final InputStream i, final Queue<String> q) {
        this.in = i;
        this.queue = q;
        run = false;
    }

    @Override
    public void run() {
        synchronized (this) {
            run = true;
            this.notifyAll();
        }
        String line = null;
        try (final BufferedReader sc = new BufferedReader(
                new InputStreamReader(in, Charset.defaultCharset()))) {
            while (run) {
                line = sc.readLine();
                if (line != null) {
                    queue.add(line);
                }
            }
        } catch (IOException ex) {
            // TODO
        }
    }

    /**
     * Call to halt running.
     *
     * @throws IOException
     *                         when there was an issue with closing the input
     *                         strem
     */
    public void stop() throws IOException {
        run = false;
        in.close();
    }

    /**
     * Determines whether or not it is running.
     *
     * @return true if running
     */
    public boolean isRunning() {
        return run;
    }

}
