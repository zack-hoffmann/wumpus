package wumpus.engine.entity;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HashSet-based in-memory entity storage.
 */
public final class MemoryEntityStore implements EntityStore {

    /**
     * Bit offset to make space for nanoseconds.
     */
    private static final int MILLIS_BIT_OFFSET = 20;

    /**
     * Mask to remove the millisecond digits from nanoseconds.
     */
    private static final long NANO_MILLIS_MASK = 9223372036854251520L;

    /**
     * Entity storage.
     */
    private final Set<Entity> entities;

    /**
     * Construct the store. Uses a simple empty HashSet.
     */
    public MemoryEntityStore() {
        entities = ConcurrentHashMap.newKeySet();
    }

    @Override
    public EntityStream stream() {
        return new EntityStream(entities.stream());
    }

    @Override
    public Optional<Entity> get(final long id) {
        return stream().filter(e -> id == e.getId()).findFirst();
    }

    @Override
    public Optional<Entity> commit(final Entity e) {
        entities.add(e);
        return get(e.getId());
    }

    @Override
    public Entity create() {
        final Entity e = new Entity(newId());
        entities.add(e);
        return e;
    }

    /**
     * Generate a new unique ID from system time.
     *
     * @return a new entity ID
     */
    private long newId() {
        long id;
        do {
            id = (System.currentTimeMillis() << MILLIS_BIT_OFFSET)
                    | (System.nanoTime() & ~NANO_MILLIS_MASK);
        } while (get(id).isPresent());
        return id;
    }
}
