package wumpus;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convenience methods for exception-causing behaviors.
 */
public interface Mediator {

    @FunctionalInterface
    public interface RiskyConsumer<E> {

        /**
         * Process a value.
         *
         * @param e
         *              the value to be processed
         * @throws Exception
         *                       when not successful
         */
        void accept(E e) throws Exception;

        /**
         * Convenience method to attempt via mediator.
         *
         * @param e
         *              the value to be processed
         */
        default void attempt(E e) {
            Mediator.attempt(this, e);
        }

    }

    @FunctionalInterface
    public interface RiskySupplier<E> {

        /**
         * Return a value.
         *
         * @return the needed value
         * @throws Exception
         *                       when the value cannot be returned
         */
        E get() throws Exception;

    }

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

    static <I> Optional<I> attemptAndReturn(final RiskyConsumer<I> con,
            final I in) {
        try {
            con.accept(in);
            return Optional.of(in);
        } catch (final Exception e) {
            Logger.getLogger(Mediator.class.getName()).log(Level.WARNING,
                    "Failed attempt.", e);
            return Optional.empty();
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
            final O o = sup.get();
            if (o == null) {
                throw new RuntimeException("Required value is null.");
            }
            return o;
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
            throw new RuntimeException("Unrecoverable error.", e);
        }
    }

}
