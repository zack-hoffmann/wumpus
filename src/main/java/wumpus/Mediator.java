package wumpus;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convenience methods for exception-causing behaviors.
 */
public interface Mediator {

    /**
     * Attempt to get a value where the value is not essential. A warning will
     * be logged for failed attempts.
     *
     * @param sup
     *                the supplier source of the value
     * @param <O>
     *                supplier output type
     * @return an optional reference to the value
     */
    static <O> Optional<O> attempt(final RiskySupplier<O> sup) {
        try {
            return Optional.ofNullable(sup.get());
        } catch (final Exception e) {
            Logger.getLogger(Mediator.class.getName()).log(Level.WARNING,
                    "Failed attempt.", e);
            return Optional.empty();
        }
    }

    /**
     * Attempt to process a value where the processing is not essential. A
     * warning will be logged for failed attempts.
     *
     * @param con
     *                the consumer for the value
     * @param in
     *                the value to process
     * @param <I>
     *                consumer input type
     */
    static <I> void attempt(final RiskyConsumer<I> con, final I in) {
        try {
            con.accept(in);
        } catch (final Exception e) {
            Logger.getLogger(Mediator.class.getName()).log(Level.WARNING,
                    "Failed attempt.", e);
        }
    }

    /**
     * Obtain a required value or abort the application.
     *
     * @param sup
     *                the supplier source of the value
     * @param <O>
     *                supplier output type
     * @return the required value
     */
    static <O> O require(final RiskySupplier<O> sup) {
        try {
            return sup.get();
        } catch (final Exception e) {
            Logger.getLogger(Mediator.class.getName()).log(Level.WARNING,
                    "Failed require.", e);
            throw new RuntimeException("Unrecoverable error.", e);
        }
    }

    /**
     * Perform a required value processing or abort the application.
     *
     * @param con
     *                the consumer for the value
     * @param in
     *                the value to process
     * @param <I>
     *                consumer input type
     */
    static <I> void require(final RiskyConsumer<I> con, final I in) {
        try {
            con.accept(in);
        } catch (final Exception e) {
            Logger.getLogger(Mediator.class.getName()).log(Level.WARNING,
                    "Failed require.", e);
        }
    }

}
