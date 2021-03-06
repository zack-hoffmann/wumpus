package wumpus.engine.entity;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import wumpus.engine.entity.component.Component;
import wumpus.engine.entity.component.Expired;

/**
 * A {@link Stream} of Entities, with helper methods for accessing components.
 */
public final class EntityStream implements Stream<Entity> {

    /**
     * Helper class for collecting a regular stream of entities to an
     * EntityStream.
     */
    private static class EntityStreamCollector
            implements Collector<Entity, Set<Entity>, EntityStream> {

        /**
         * The store this stream is wrapping.
         */
        private final EntityStore store;

        /**
         * Initialize the collector.
         *
         * @param s
         *              the store wrapped by the stream
         */
        EntityStreamCollector(final EntityStore s) {
            this.store = s;
        }

        @Override
        public Supplier<Set<Entity>> supplier() {
            return () -> new HashSet<Entity>();
        }

        @Override
        public BiConsumer<Set<Entity>, Entity> accumulator() {
            return (s, e) -> s.add(e);
        }

        @Override
        public BinaryOperator<Set<Entity>> combiner() {
            return (s1, s2) -> {
                s1.addAll(s2);
                return s1;
            };
        }

        @Override
        public Function<Set<Entity>, EntityStream> finisher() {
            return s -> new EntityStream(s.stream(), store);
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Collector.Characteristics.UNORDERED);
        }

    }

    /**
     * Provides a static collector for pulling the elements of a stream in to an
     * entity stream.
     *
     * Note that since collecting is a terminal operation, using this will load
     * the entirety of the stream, so do not use on excessively large streams.
     *
     * @param s
     *              the store to wrap
     * @return a collector of an "stream of entities" to an "entity stream"
     */
    public static Collector<Entity, Set<Entity>, EntityStream> collector(
            final EntityStore s) {
        return new EntityStreamCollector(s);
    }

    /**
     * The underlying Java Stream this wraps.
     */
    private final Stream<Entity> delegate;

    /**
     * The store this stream is wrapping.
     */
    private final EntityStore store;

    /**
     * Create this stream wrapping another stream of entities.
     *
     * Will exclude expired entities.
     *
     * @param d
     *              the stream to be wrapped
     * @param s
     *              the store to stream from
     */
    public EntityStream(final Stream<Entity> d, final EntityStore s) {
        this.delegate = d.filter(e -> !e.hasComponent(Expired.class));
        this.store = s;
    }

    /**
     * Filter this stream for entities which have a given component and return a
     * stream of those components.
     *
     * @param clazz
     *                  the component to match
     * @param <C>
     *                  the component type to match
     * @return a stream of those components from the given entities
     */
    public <C extends Component> Stream<C> component(final Class<C> clazz) {
        return delegate.filter(e -> e.hasComponent(clazz))
                .map(e -> e.component(clazz));
    }

    /**
     * Filter this stream for entities which have all of the given components
     * and return a stream of Maps of those components.
     *
     * @param clazzes
     *                    The components to match
     * @return A stream of maps of those components, mapped by component type
     */
    public Stream<Entity.ComponentMap> components(
            final Set<Class<? extends Component>> clazzes) {
        return delegate
                .filter(e -> clazzes.stream().map(z -> e.hasComponent(z))
                        .reduce(true, (i, d) -> i && d))
                .map(e -> e.componentMap());
    }

    /**
     * Stream the contents of selected entities.
     *
     * @return entity stream of the contents of the stream
     */
    public EntityStream contents() {
        return store.stream(this.map(e -> e.contents())
                .flatMap(sl -> sl.stream()).collect(Collectors.toSet()));
    }

    @Override
    public Iterator<Entity> iterator() {
        return delegate.iterator();
    }

    @Override
    public Spliterator<Entity> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public boolean isParallel() {
        return delegate.isParallel();
    }

    @Override
    public Stream<Entity> sequential() {
        return delegate.sequential();
    }

    @Override
    public Stream<Entity> parallel() {
        return delegate.parallel();
    }

    @Override
    public Stream<Entity> unordered() {
        return delegate.unordered();
    }

    @Override
    public Stream<Entity> onClose(final Runnable closeHandler) {
        return delegate.onClose(closeHandler);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public Stream<Entity> filter(final Predicate<? super Entity> predicate) {
        return delegate.filter(predicate);
    }

    @Override
    public <R> Stream<R> map(
            final Function<? super Entity, ? extends R> mapper) {
        return delegate.map(mapper);
    }

    @Override
    public IntStream mapToInt(final ToIntFunction<? super Entity> mapper) {
        return delegate.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(final ToLongFunction<? super Entity> mapper) {
        return delegate.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(
            final ToDoubleFunction<? super Entity> mapper) {
        return delegate.mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(
            final Function<? super Entity, ? extends Stream<? extends R>> //
            mapper) {
        return delegate.flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(
            final Function<? super Entity, ? extends IntStream> mapper) {
        return delegate.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(
            final Function<? super Entity, ? extends LongStream> mapper) {
        return delegate.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(
            final Function<? super Entity, ? extends DoubleStream> mapper) {
        return delegate.flatMapToDouble(mapper);
    }

    @Override
    public Stream<Entity> distinct() {
        return delegate.distinct();
    }

    @Override
    public Stream<Entity> sorted() {
        return delegate.sorted();
    }

    @Override
    public Stream<Entity> sorted(final Comparator<? super Entity> comparator) {
        return delegate.sorted(comparator);
    }

    @Override
    public Stream<Entity> peek(final Consumer<? super Entity> action) {
        return delegate.peek(action);
    }

    @Override
    public Stream<Entity> limit(final long maxSize) {
        return delegate.limit(maxSize);
    }

    @Override
    public Stream<Entity> skip(final long n) {
        return delegate.skip(n);
    }

    @Override
    public void forEach(final Consumer<? super Entity> action) {
        delegate.forEach(action);
    }

    @Override
    public void forEachOrdered(final Consumer<? super Entity> action) {
        delegate.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <A> A[] toArray(final IntFunction<A[]> generator) {
        return delegate.toArray(generator);
    }

    @Override
    public Entity reduce(final Entity identity,
            final BinaryOperator<Entity> accumulator) {
        return delegate.reduce(identity, accumulator);
    }

    @Override
    public Optional<Entity> reduce(final BinaryOperator<Entity> accumulator) {
        return delegate.reduce(accumulator);
    }

    @Override
    public <U> U reduce(final U identity,
            final BiFunction<U, ? super Entity, U> accumulator,
            final BinaryOperator<U> combiner) {
        return delegate.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(final Supplier<R> supplier,
            final BiConsumer<R, ? super Entity> accumulator,
            final BiConsumer<R, R> combiner) {
        return delegate.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(final Collector<? super Entity, A, R> collector) {
        return delegate.collect(collector);
    }

    @Override
    public Optional<Entity> min(final Comparator<? super Entity> comparator) {
        return delegate.min(comparator);
    }

    @Override
    public Optional<Entity> max(final Comparator<? super Entity> comparator) {
        return delegate.max(comparator);
    }

    @Override
    public long count() {
        return delegate.count();
    }

    @Override
    public boolean anyMatch(final Predicate<? super Entity> predicate) {
        return delegate.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(final Predicate<? super Entity> predicate) {
        return delegate.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(final Predicate<? super Entity> predicate) {
        return delegate.noneMatch(predicate);
    }

    @Override
    public Optional<Entity> findFirst() {
        return delegate.findFirst();
    }

    @Override
    public Optional<Entity> findAny() {
        return delegate.findAny();
    }

}
