package wumpus;

import java.util.function.Supplier;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.component.Component;

/**
 * Utilities used for various tests.
 */
public final class Testing {

    /**
     * Mock component type.
     */
    public static final class MockComponent implements Component {

        /**
         * Arbitrary value for identifying the component.
         */
        private final Object value;

        /**
         * No-op.
         */
        private MockComponent() {
            value = new Object();
        }

        /**
         * Mock component with arbitrary identifying value.
         *
         * @param a
         *              an arbitrary identifying value
         */
        private MockComponent(final Object a) {
            this.value = a;
        }

        @Override
        public boolean equals(final Object x) {
            return x instanceof MockComponent && //
                    value.equals(((MockComponent) x).value);
        }

        @Override
        public int hashCode() {
            return MockComponent.class.getName().hashCode() + value.hashCode();
        }
    }

    /**
     * Supplies an entity with an illegal ID.
     *
     * @return a supplier of an entity with illegal ID -1L
     */
    public static Supplier<Entity> badEntitySupplier() {
        return () -> new Entity(-1L);
    }

    /**
     * Get a mock component with no value.
     *
     * @return a mock component
     */
    public static MockComponent getMockComponent() {
        return new MockComponent();
    }

    /**
     * Get a mock component with a value.
     *
     * @param f
     *              a value to store in the component
     * @return a mock component with the stored valued
     */
    public static MockComponent getMockComponent(final Object f) {
        return new MockComponent(f);
    }

    /**
     * Concealed constructor. Do not use.
     */
    private Testing() {
        throw new UnsupportedOperationException("Do not instantiate.");
    }
}
