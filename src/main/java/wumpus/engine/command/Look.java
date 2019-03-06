package wumpus.engine.command;

import java.util.OptionalLong;
import java.util.Set;

import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Examining;
import wumpus.engine.entity.component.Physical;

/**
 * Examine an entity.
 */
public final class Look implements Command {

    @Override
    public String exec(final long source, final EntityStore store,
            final String... args) {
        final Entity e = store.get(source).get();
        final OptionalLong loc = e.getComponent(Physical.class).getLocation();
        if (loc.isEmpty() || store.get(loc.getAsLong()).isEmpty()) {
            return "You are not sure where you are...";
        } else {
            e.registerComponent(new Examining(loc.getAsLong()));
            store.commit(e);
            return "You take a look around";
        }
    }

    @Override
    public String getName() {
        return "look";
    }

    @Override
    public Set<String> getAliases() {
        return Set.of("l");
    }

}