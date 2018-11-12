package wumpus;

import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Testing wumpus.App excluding invocation of the main method.
 */
public class AppTest {

    /**
     * App produces output.
     */
    @Test
    public final void printsOutput() {
        final ByteArrayOutputStream outStr = new ByteArrayOutputStream();
        final PrintStream out = new PrintStream(outStr);
        new App(out).run();
        out.close();
        assertFalse(new String(outStr.toByteArray()).isEmpty());
    }
}
