package wumpus.engine.entity;

import java.util.Optional;
import java.util.Set;

/**
 * Interface to concrete entity storage.
 */
public interface EntityStore {

    /**
     * Retrieves a stream reference to all entities.
     *
     * This must be used carefully, as it will maintain an open connection to
     * the storage when possible.
     *
     * @return stream reference to all entities.
     */
    EntityStream stream();

    /**
     * Retrieves a stream reference to a set of entities.
     *
     * @param ids
     *                the entity IDs to stream.
     * @return stream reference to the given entities.
     */
    EntityStream stream(Set<Long> ids);

    /**
     * Retrieves an individual entity by ID.
     *
     * @param id
     *               ID of the entity to retrieve.
     * @return an optional reference to the entity, present if the entity
     *         exists.
     */
    Optional<Entity> get(final long id);

    /**
     * Attempts to store the passed entity.
     *
     * The existing entity with the same ID will be atomically replaced with
     * this one.
     *
     * @param e
     *              the entity to store
     * @return an optional reference to the entity as it is stored, present if
     *         stored, absent if the entity was not stored.
     */
    Optional<Entity> commit(final Entity e);

    /**
     * Generates a new entity.
     *
     * @return a new entity with a newly generated ID.
     */
    Entity create();
}
