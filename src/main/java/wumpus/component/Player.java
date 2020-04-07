package wumpus.component;

@FunctionalInterface
public interface Player extends ValueComponent<String> {

    default String playerName() {
        return value();
    }

}