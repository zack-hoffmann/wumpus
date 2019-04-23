package wumpus.engine.entity.component;

import java.util.Set;

/**
 * Describes an entity which is controlled by a player.
 */
public final class Player extends AbstractEntityComponent {

    /**
     * A container entity ID for the items the player is holding.
     */
    private final long inventory;

    /**
     * Create a player with a given inventory.
     *
     * @param i
     *              the entity ID of the inventory container
     */
    public Player(final long i) {
        this.inventory = i;
    }

    /**
     * Get the entity ID of the player's inventory.
     *
     * @return the entity ID of the inventory
     */
    public long getInventory() {
        return inventory;
    }

    @Override
    public Set<Component> dependencies() {
        return Set.of(new Physical());
    }
}
