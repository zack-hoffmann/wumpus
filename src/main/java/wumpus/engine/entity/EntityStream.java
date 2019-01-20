package wumpus.engine.entity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
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

public class EntityStream implements Stream<Entity> {

    private final Stream<Entity> delegate;

    public EntityStream(final Stream<Entity> d) {
        this.delegate = d;
    }

    public <C extends Component> Stream<C> component(final Class<C> clazz) {
        return delegate.filter(e -> e.hasComponent(clazz)).map(e -> e.getComponent(clazz));
    }

    public Stream<Map<Class<? extends Component>, Component>> components(final Set<Class<? extends Component>> cs) {
        Stream<Entity> inter = delegate;
        for (Class<? extends Component> z : cs) {
            inter = delegate.filter(e -> e.hasComponent(z));
        }
        Function<Entity, Map<Class<? extends Component>, Component>> mapper = e -> e.getComponents().stream()
                .filter(com -> cs.contains(com.getClass()))
                .collect(Collectors.toMap(com -> com.getClass(), Function.identity()));

        return inter.map(mapper);
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
    public Stream<Entity> onClose(Runnable closeHandler) {
        return delegate.onClose(closeHandler);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public Stream<Entity> filter(Predicate<? super Entity> predicate) {
        return delegate.filter(predicate);
    }

    @Override
    public <R> Stream<R> map(Function<? super Entity, ? extends R> mapper) {
        return delegate.map(mapper);
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super Entity> mapper) {
        return delegate.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super Entity> mapper) {
        return delegate.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super Entity> mapper) {
        return delegate.mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super Entity, ? extends Stream<? extends R>> mapper) {
        return delegate.flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(Function<? super Entity, ? extends IntStream> mapper) {
        return delegate.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super Entity, ? extends LongStream> mapper) {
        return delegate.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super Entity, ? extends DoubleStream> mapper) {
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
    public Stream<Entity> sorted(Comparator<? super Entity> comparator) {
        return delegate.sorted(comparator);
    }

    @Override
    public Stream<Entity> peek(Consumer<? super Entity> action) {
        return delegate.peek(action);
    }

    @Override
    public Stream<Entity> limit(long maxSize) {
        return delegate.limit(maxSize);
    }

    @Override
    public Stream<Entity> skip(long n) {
        return delegate.skip(n);
    }

    @Override
    public void forEach(Consumer<? super Entity> action) {
        delegate.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super Entity> action) {
        delegate.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return delegate.toArray(generator);
    }

    @Override
    public Entity reduce(Entity identity, BinaryOperator<Entity> accumulator) {
        return delegate.reduce(identity, accumulator);
    }

    @Override
    public Optional<Entity> reduce(BinaryOperator<Entity> accumulator) {
        return delegate.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super Entity, U> accumulator, BinaryOperator<U> combiner) {
        return delegate.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Entity> accumulator, BiConsumer<R, R> combiner) {
        return delegate.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super Entity, A, R> collector) {
        return delegate.collect(collector);
    }

    @Override
    public Optional<Entity> min(Comparator<? super Entity> comparator) {
        return delegate.min(comparator);
    }

    @Override
    public Optional<Entity> max(Comparator<? super Entity> comparator) {
        return delegate.max(comparator);
    }

    @Override
    public long count() {
        return delegate.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super Entity> predicate) {
        return delegate.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super Entity> predicate) {
        return delegate.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super Entity> predicate) {
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