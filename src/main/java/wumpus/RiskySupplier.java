package wumpus;

/**
 * A variant of a supplier which may throw an exception. To be used in
 * conjuction with Mediator.
 *
 * @param <E>
 *                the value type to be supplied
 */
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
