package wumpus;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import wumpus.system.WebServer;

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
     * Time to wait for some tests to complete, due to async delays.
     */
    private static final int TIMEOUT = 5000;

    /**
     * Context configuration.
     */
    private static final Application MOCK_CTX = Mock.Application.create()
            .withProperty("web.server.port", Integer.toString(TEST_PORT))
            .withProperty("web.server.context.path", "/")
            .withProperty("web.server.servlet.path.spec", "/*")
            .withProperty("web.server.keystore.path", "teststore.jks")
            .withProperty("web.server.keystore.password", "abcd1234");

    /**
     * Set up test trust store.
     */
    @BeforeClass
    public static void setUpTrust() {
        System.setProperty("javax.net.ssl.trustStore", "target/test-classes/"
                + MOCK_CTX.requiredProperty("web.server.keystore.path"));
        System.setProperty("javax.net.ssl.trustStorePassword",
                MOCK_CTX.requiredProperty("web.server.keystore.password"));
    }

    /**
     * Server starts without exception.
     */
    @Test
    public void starts() {
        final WebServer ws = WebServer.create.get();
        ws.stop();
    }

    /**
     * Server enters running status.
     */
    @Test
    public void runs() {
        final WebServer ws = WebServer.create.get();
        Assert.assertTrue(ws.isRunning());
        ws.stop();
    }

    /**
     * Server is not in running status after stop.
     */
    @Test
    public void stops() {
        final WebServer ws = WebServer.create.get();
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
        final WebServer ws = WebServer.create.get();
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
        final WebServer ws = WebServer.create.get();

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
        // TODO need new test for this.

        final WebServer ws = WebServer.create.get();

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
    @Test(timeout = TIMEOUT)
    public void serverSend() throws Exception {
        final AtomicReference<String> ref = new AtomicReference<>("");
        final CountDownLatch latch = new CountDownLatch(1);

        // TODO need new test for this.

        final WebSocketAdapter receiver = new WebSocketAdapter() {
            @Override
            public void onWebSocketText(final String message) {
                ref.set(message);
                latch.countDown();
            }
        };
        final WebServer ws = WebServer.create.get();

        final WebSocketClient client = new WebSocketClient();
        client.start();
        final Future<Session> fut = client.connect(receiver,
                new URI("wss://" + TEST_HOST + ":" + TEST_PORT + "/test"));
        fut.get();
        latch.await();
        client.stop();
        ws.stop();

        Assert.assertEquals(TEST_MESSAGE, ref.get());
    }
}
