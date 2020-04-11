package wumpus.function;

import java.util.Optional;

@FunctionalInterface
public interface ListNode {

    static ListNode of(final Object o1, final ListNode o2) {
        return i -> Optional.ofNullable(
                Optional.of(i).filter(n -> n == 0).map(n -> o1).orElse(o2));
    }

    Optional<Object> get(final int i);

    default ListNode cons() {
        return i -> Optional.empty();
    }

    default ListNode cons(final Object x) {
        return ListNode.of(x, null);
    }

    default ListNode cons(final Object x, final ListNode xs) {
        return ListNode.of(x, xs);
    }

    default Object head() {
        return get(0).get();
    }

    default ListNode tail() {
        return get(1).filter(o -> (o instanceof ListNode))
                .map(o -> ((ListNode) o)).get();
    }

    default boolean isEmpty() {
        return get(0).isEmpty();
    }

    default int length() {
        return Optional.of(this).filter(t -> !t.isEmpty())
                .map(t -> 1 + t.tail().length()).orElse(0);
    }
}