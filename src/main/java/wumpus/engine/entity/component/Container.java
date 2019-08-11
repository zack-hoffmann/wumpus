package wumpus.engine.entity.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Component for holding other entities.
 */
public final class Container extends AbstractEntityComponent {

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
     * Build a container from another container.
     *
     * @param c
     *               the original container to build from
     * @param cs
     *               additional entities to add to the container
     */
    public Container(final Container c, final long... cs) {
        contents = Stream
                .concat(c.contents().stream(), Arrays.stream(cs).boxed())
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Build a container from a subset of another container.
     *
     * @param c
     *                the original container to build from
     * @param rem
     *                the condition of entities to keep
     */
    public Container(final Container c, final Predicate<Long> rem) {
        contents = c.contents().stream().filter(rem)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Retrieve the contents of the container.
     *
     * @return the contents of the container
     */
    public Set<Long> contents() {
        return contents;
    }

    @Override
    public List<String> debug() {
        return List.of("[" + contents.stream().map(l -> l.toString())
                .collect(Collectors.joining(",")) + "]");
    }
}
