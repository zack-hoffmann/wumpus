package wumpus.component;

@FunctionalInterface
public interface Player extends Component<String> {

    default String playerName() {
        return value();
    }

}