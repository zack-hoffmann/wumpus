package wumpus.io;

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

}
