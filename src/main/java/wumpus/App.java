package wumpus;

import java.io.PrintStream;

/**
 * Application instance for the wumpus game.
 */
public class App implements Runnable {

    /**
     * Output stream used by this App instance.
     */
    private final PrintStream out;

    /**
     * Runs a new wumpus App instance using standard output.
     *
     * @param args
     *                 Command line arguments. None specified at this time.
     */
    public static void main(final String... args) {
        new App(System.out).run();
    }

    /**
     * Set up a new instance of the wumpus App with an output stream.
     *
     * @param o
     *              A print stream for use by App output.
     */
    public App(final PrintStream o) {
        this.out = o;
    }

    @Override
    public final void run() {
        out.println("Welcome to Hunt the Wumpus by Zack Hoffmann!");
    }
}
