package wumpus;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.BeforeClass;
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
     * Test message to send.
     */
    private static final String TEST_MESSAGE = "foo";

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
        ctx.put("web.server.keystore.path", "teststore.jks");
        ctx.put("web.server.keystore.password", "abcd1234");
        return s -> {
            return Optional.ofNullable(ctx.get(s));
        };
    }

    /**
     * A mock consumer for not handling any web socket messages.
     */
    private static final Consumer<String> NOOP_MSG_HANDLER = s -> {
    };

    /**
     * A mock consumer for not handling any web socket messages.
     */
    private static final Consumer<Session> NOOP_CONN_HANDLER = s -> {
    };

    /**
     * Set up test trust store.
     */
    @BeforeClass
    public static void setUpTrust() {
        final Context ctx = getContext();
        System.setProperty("javax.net.ssl.trustStore", "target/test-classes/"
                + ctx.requiredProperty("web.server.keystore.path"));
        System.setProperty("javax.net.ssl.trustStorePassword",
                ctx.requiredProperty("web.server.keystore.password"));
    }

    /**
     * Server starts without exception.
     */
    @Test
    public void starts() {
        final WebServer ws = WebServer.serve(getContext(), NOOP_CONN_HANDLER,
                NOOP_MSG_HANDLER);
        ws.stop();
    }

    /**
     * Server enters running status.
     */
    @Test
    public void runs() {
        final WebServer ws = WebServer.serve(getContext(), NOOP_CONN_HANDLER,
                NOOP_MSG_HANDLER);
        Assert.assertTrue(ws.isRunning());
        ws.stop();
    }

    /**
     * Server is not in running status after stop.
     */
    @Test
    public void stops() {
        final WebServer ws = WebServer.serve(getContext(), NOOP_CONN_HANDLER,
                NOOP_MSG_HANDLER);
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
        final WebServer ws = WebServer.serve(getContext(), NOOP_CONN_HANDLER,
                NOOP_MSG_HANDLER);
        try (Socket s = new Socket(TEST_HOST, TEST_PORT)) {
            Assert.assertNotNull(s);
        }
        ws.stop();
    }

    /**
     * Client can connect while server is running.
     *
     * @throws Exception
     *                       when there is a connection issue
     */
    @Test
    public void clientConnect() throws Exception {
        final WebServer ws = WebServer.serve(getContext(), NOOP_CONN_HANDLER,
                NOOP_MSG_HANDLER);

        final WebSocketClient client = new WebSocketClient();
        client.start();
        final Future<Session> fut = client.connect(new WebSocketAdapter(),
                new URI("wss://" + TEST_HOST + ":" + TEST_PORT + "/test"));
        fut.get();
        client.stop();
        ws.stop();
    }

    /**
     * Server receives messages and executes the appropriate handler.
     *
     * @throws Exception
     *                       when there is a connection issue
     */
    @Test
    public void serverReceive() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>("");
        final Consumer<String> cons = s -> {
            ref.set(s);
        };

        final WebServer ws = WebServer.serve(getContext(), NOOP_CONN_HANDLER,
                cons);

        final WebSocketClient client = new WebSocketClient();
        client.start();
        final Future<Session> fut = client.connect(new WebSocketAdapter(),
                new URI("wss://" + TEST_HOST + ":" + TEST_PORT + "/test"));
        fut.get().getRemote().sendString(TEST_MESSAGE);
        client.stop();
        ws.stop();

        Assert.assertEquals(TEST_MESSAGE, ref.get());
    }

    /**
     * Server sends messages.
     *
     * @throws Exception
     *                       when there is a connection issue
     */
    @Test
    public void serverSend() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>("");

        final RiskyConsumer<Session> cons = x -> x.getRemote()
                .sendString(TEST_MESSAGE);

        final WebSocketAdapter receiver = new WebSocketAdapter() {
            @Override
            public void onWebSocketText(final String message) {
                ref.set(message);
            }
        };
        final WebServer ws = WebServer.serve(getContext(), cons::attempt,
                NOOP_MSG_HANDLER);

        final WebSocketClient client = new WebSocketClient();
        client.start();
        final Future<Session> fut = client.connect(receiver,
                new URI("wss://" + TEST_HOST + ":" + TEST_PORT + "/test"));
        fut.get();
        client.stop();
        ws.stop();

        Assert.assertEquals(TEST_MESSAGE, ref.get());
    }
}
