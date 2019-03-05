package wumpus.engine.entity.component;

/**
 * An item component.
 */
public final class Item extends AbstractEntityComponent {

    /**
     * The quantity of the item.
     */
    private final int count;

    /**
     * Create an item with a count.
     *
     * @param c
     *              the count of this item
     */
    public Item(final int c) {
        this.count = c;
    }

    /**
     * Create an item based on another item with a difference in count.
     *
     * @param i
     *              the existing item to base this on
     * @param d
     *              the difference to the count
     */
    public Item(final Item i, final int d) {
        this(i.getCount() + d);
    }

    /**
     * Get the count of this item.
     *
     * @return the count of this item
     */
    public int getCount() {
        return count;
    }
}
