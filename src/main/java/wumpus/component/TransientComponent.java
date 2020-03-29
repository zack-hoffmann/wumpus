package wumpus.component;

import wumpus.Component;

/**
 * Component which should not be kept in persistent storage.
 */
public interface TransientComponent<V> extends Component<V> {

}