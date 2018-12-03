package wumpus.engine.entity.component;

import java.util.Map;

/**
 * A travelable location in the game world.
 */
public final class Location implements Component {

    /**
     * Locations linked to this one, by label relative to this location.
     */
    private final Map<String, Integer> linkedLocations;

    /**
     * Initialize location with links.
     *
     * @param l the linked locations for this location.
     */
    public Location(final Map<String, Integer> l) {
        this.linkedLocations = l;
    }

    /**
     * Retrieve linked locations.
     *
     * @return map of linked locations,
     */
    public Map<String, Integer> getLinkedLocations() {
        return linkedLocations;
    }
}
