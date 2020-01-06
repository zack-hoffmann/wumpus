package wumpus;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A memory management wrapper for Strings. This allows for some de-duplication
 * of strings in memory by ensuring string uniqueness within a given pool. Pools
 * are given a fixed size and when the size is exceeded the last string to have
 * been interned is "forgotten".
 */
@FunctionalInterface
public interface StringPool {

    /**
     * Create a new pool. This will not store the pool by name.
     *
     * @param size
     *                 the size of the pool to create
     * @return a new pool with the given size
     */
    static StringPool construct(final int size) {
        final Map<String, String> map = new ConcurrentHashMap<>(size + 1);
        final Queue<String> queue = new ConcurrentLinkedQueue<>();
        return i -> intern(map, queue, size, i);
    }

    /**
     * Helper method for the interning process.
     *
     * @param map
     *                  the map used to hold the string in memory
     * @param queue
     *                  the queue used to track ordering for expiry
     * @param size
     *                  the size of the pool
     * @param i
     *                  the string to intern
     * @return the interned string
     */
    static String intern(final Map<String, String> map,
            final Queue<String> queue, final int size, final String i) {
        // If not already interned then add to pool
        if (!map.keySet().contains(i)) {
            map.put(i, i);
        } else {
            // If already interned then remove from current expiry location
            queue.remove(i);
        }

        // Get interned value and put it at the tail of expiry
        String q = map.get(i);
        queue.add(q);

        // If size has been exceeded then remove tail of expiry from
        // both
        if (map.size() > size) {
            var x = queue.poll();
            map.remove(x);
        }
        return q;
    }

    /**
     * For the given string, determine if it or an equal one (as
     * {@link java.lang.String#equals(Object)}) already resides in the pool and
     * if so then return the instance residing in the pool. If not then place
     * the given string in the pool and return it. Strings are tracked in the
     * order they were most recently interned and when the pool size would be
     * exceeded the oldest interned string (without being re-interned) is
     * discarded.
     *
     * @param e
     *              the string to intern
     * @return the equal string interned or the newly interned string itself
     */
    String intern(String e);

    /**
     * Generate and intern a stringified UUID.
     *
     * @return new UUID token
     */
    default String newToken() {
        return intern(UUID.randomUUID().toString());
    }
}
