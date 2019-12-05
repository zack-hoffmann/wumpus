package wumpus;

/**
 * A variant of a consumer which may throw an excepton. To be used in
 * conjunction with Mediator.
 *
 * @param <E>
 *                the value type to be consumed
 */
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

}
