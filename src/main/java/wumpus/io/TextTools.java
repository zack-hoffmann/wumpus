package wumpus.io;

/**
 * Utility class to convert input/output text.
 */
public final class TextTools {

    /**
     * Private constructor to prevent instantiation.
     */
    private TextTools() {

    }

    /**
     * Capitalize the first letter of a string.
     *
     * @param i
     *              the string to capitalize
     * @return the string with the first letter capitalized
     */
    public static String capitalize(final String i) {
        return i.substring(0, 1).toUpperCase() + i.substring(1);
    }
}
