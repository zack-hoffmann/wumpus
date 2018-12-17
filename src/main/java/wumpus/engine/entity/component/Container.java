package wumpus.engine.entity.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Component for holding other entities.
 */
public final class Container implements Component {

    /**
     * The contents of this entity.
     */
    private final Set<Long> contents;

    /**
     * Build a container with no entities.
     */
    public Container() {
        contents = Collections.unmodifiableSet(new HashSet<>());
    }

    /**
     * Build a container with a variable number of entities.
     *
     * @param cs
     *               the entities to add to the container
     */
    public Container(final long... cs) {
        contents = Arrays.stream(cs).boxed()
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Retrieve the contents of the container.
     *
     * @return the contents of the container
     */
    public Set<Long> getContents() {
        return contents;
    }
}
