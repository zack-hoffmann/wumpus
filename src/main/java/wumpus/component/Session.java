package wumpus.component;

import java.util.function.Consumer;

import wumpus.Message;

public interface Session extends TransientComponent<Consumer<Message>> {

}