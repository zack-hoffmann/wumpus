package wumpus.engine.command;

import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Examining;
import wumpus.engine.entity.component.Player;

/**
 * Command to examine the player's inventory.
 */
public final class Inventory implements Command {

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        final Entity e = store.get(source).get();
        final long inventory = e.component(Player.class).inventory();
        e.registerComponent(new Examining(inventory));
        store.commit(e);
        return "You examine your belongings.";
    }

    @Override
    public String name() {
        return "inventory";
    }

    @Override
    public Set<String> aliases() {
        return Set.of("i");
    }

}
