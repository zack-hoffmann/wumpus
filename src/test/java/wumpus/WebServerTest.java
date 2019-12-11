package wumpus;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for the web socket server.
 */
public final class WebServerTest {

    /**
     * TCP network port to use for testing.
     */
    private static final int TEST_PORT = 8080;

    /**
     * Hostname to use for testing.
     */
    private static final String TEST_HOST = "localhost";

    /**
     * Generate a mock context instance.
     *
     * @return a mock context instance
     */
    private static Context getContext() {
        final Map<String, String> ctx = new HashMap<String, String>();
        ctx.put("web.server.port", Integer.toString(TEST_PORT));
        ctx.put("web.server.context.path", "/");
        ctx.put("web.server.servlet.path.spec", "/*");
        return s -> {
            return Optional.ofNullable(ctx.get(s));
        };
    }

    /**
     * A mock consumer for not handling any web socket messages.
     */
    private static final RiskyConsumer<String> NOOP_HANDLER = s -> {
    };

    /**
     * Server starts without exception.
     */
    @Test
    public void starts() {
        final WebServer ws = WebServer.serve(getContext(), NOOP_HANDLER);
        ws.stop();
    }

    /**
     * Server enters running status.
     */
    @Test
    public void runs() {
        final WebServer ws = WebServer.serve(getContext(), NOOP_HANDLER);
        Assert.assertTrue(ws.isRunning());
        ws.stop();
    }

    /**
     * Server is not in running status after stop.
     */
    @Test
    public void stops() {
        final WebServer ws = WebServer.serve(getContext(), NOOP_HANDLER);
        ws.stop();
        Assert.assertFalse(ws.isRunning());
    }

    /**
     * Server is listening on the specified port while running.
     *
     * @throws UnknownHostException
     *                                  if the test host cannot be connected to
     * @throws IOException
     *                                  if the connection is refused
     */
    @Test
    public void listens() throws UnknownHostException, IOException {
        final WebServer ws = WebServer.serve(getContext(), NOOP_HANDLER);
        try (Socket s = new Socket(TEST_HOST, TEST_PORT)) {
            Assert.assertNotNull(s);
        }
        ws.stop();
    }

    // TODO full handler test with client
}
