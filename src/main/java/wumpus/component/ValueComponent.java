package wumpus.component;

@FunctionalInterface
public interface ValueComponent<V> extends Component {

    V value();
}