package wumpus.external;

/**
 * Convenience methods for exception-causing behaviors.
 */
public interface Mediator {

    class ServiceError extends RuntimeException {

        private static final long serialVersionUID = -7782609401052424457L;

        public ServiceError(final String m) {
            super(m);
        }

        public ServiceError(final String m, final Throwable c) {
            super (m, c);
        }
    }

    class ApplicationError extends RuntimeException {

        private static final long serialVersionUID = -7782609401052424457L;

        public ApplicationError(final String m) {
            super(m);
        }

        public ApplicationError(final String m, final Throwable c) {
            super (m, c);
        }
    }

    @FunctionalInterface
    interface RiskyConsumer<E> {

        void accept(E e) throws Exception;

    }

    @FunctionalInterface
    interface RiskySupplier<E> {

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
    static <O> O attempt(final RiskySupplier<O> sup) {
        try {
            return sup.get();
        } catch (final Exception e) {
            ServerLog.create.get().detail("Failed attempt.");
            throw new ServiceError("Failed attempt.", e);
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
            ServerLog.create.get().detail("Failed attempt.");
            throw new ServiceError("Failed attempt.", e);
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
            ServerLog.create.get().detail("Failed require.");
            throw new ApplicationError("Failed require.", e);
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
            ServerLog.create.get().detail("Failed require.");
            throw new ApplicationError("Failed require.", e);
        }
    }

}
