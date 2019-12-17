package wumpus;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.WebSocketContainer;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
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
        ctx.put("web.server.keystore.path", "teststore.jks");
        ctx.put("web.server.keystore.password", "abcd1234");
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
/*
    @Test(expected = ConnectException.class)
    public void clientCannotConnect()
            throws DeploymentException, IOException, URISyntaxException {

    }
*/
    @Test
    public void clientConnect() throws Exception {
        System.setProperty("javax.net.debug", "all");
        System.setProperty("javax.net.ssl.trustStore", "target/test-classes/teststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","abcd1234");
        
        final WebServer ws = WebServer.serve(getContext(), NOOP_HANDLER);
        //TimeUnit.SECONDS.sleep(2);
        final WebSocketAdapter ep = new WebSocketAdapter(){
            @Override
            public void onWebSocketConnect(Session s) {
                System.out.println("TODO Client " + s);
            }
            @Override
            public void onWebSocketError(Throwable s) {
                System.out.println("TODO Client error " + s);
            }
            @Override
            public void onWebSocketClose(int code, String s) {
                System.out.println("TODO Client close " + code + " " + s);
            }
        };
        SslContextFactory sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setTrustAll(true); // The magic
        sslContextFactory.setValidateCerts(false);

        WebSocketClient client = new WebSocketClient();

        client.start();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        Future<Session> fut = 
        client.connect(ep,
                new URI("wss://localhost:8080/test"),request);
               //wss://echo.websocket.org/
               //wss://localhost:8080/test
               //wss://qa.sockets.stackexchange.com/
        fut.get();
        
        ws.stop();
    }
}
