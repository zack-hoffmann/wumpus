package wumpus;

import java.util.function.Supplier;

import wumpus.engine.entity.Entity;

/**
 * Utilities used for various tests.
 */
public final class Testing {

    /**
     * Supplies an entity with an illegal ID.
     *
     * @return a supplier of an entity with illegal ID -1L
     */
    public static Supplier<Entity> badEntitySupplier() {
        return () -> new Entity(-1L);
    }

    /**
     * Concealed constructor. Do not use.
     */
    private Testing() {
        throw new UnsupportedOperationException(
                "Utility class; do not instantiate.");
    }
}
