package wumpus.io;

import java.io.IOException;
import java.util.Optional;

/**
 * Interface to input/output processors.
 */
public interface IOAdapter {

    /**
     * Retrieve input.
     *
     * @return input when available (non-blocking)
     */
    Optional<String> poll();

    /**
     * Send output.
     *
     * @param o
     *              the output to send
     */
    void post(final String o);

    /**
     * Stop the runners and other required services.
     *
     * @throws IOException
     *                         if either of the runner cannot be stopped
     */
    void shutdown() throws IOException;

    /**
     * Determines if input and output streams are open.
     *
     * @return true is both are open
     */
    boolean isOpen();
}
