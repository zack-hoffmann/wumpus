package wumpus;

import java.util.function.Consumer;

import javax.servlet.Servlet;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Represents the game web socket server.
 */
@FunctionalInterface
public interface WebServer {

    /**
     * Inner class for the required web socket servlet, which must be static and
     * serializable.
     */
    class WumpusWebSocketServlet extends WebSocketServlet {

        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = -2844589799060759594L;

        /**
         * Web socket endpoint.
         */
        private final transient WebSocketAdapter ep;

        /**
         * Create new web socket servlet.
         *
         * @param connectHandler
         *                           the handler for new connections
         * @param messageHandler
         *                           the handler for the socket
         */
        private WumpusWebSocketServlet(final Consumer<Session> connectHandler,
                final Consumer<String> messageHandler) {
            this.ep = new WebSocketAdapter() {
                @Override
                public void onWebSocketConnect(final Session sess) {
                    connectHandler.accept(sess);
                }

                @Override
                public void onWebSocketText(final String message) {
                    messageHandler.accept(message);
                }
            };
        }

        @Override
        public void configure(final WebSocketServletFactory factory) {
            factory.setCreator((req, resp) -> ep);
        }

    }

    /**
     * Create and run a new web socket server. Will crash if the server cannot
     * be built or started.
     *
     * @param ctx
     *                           the application context to use
     * @param connectHandler
     *                           the handler for new connections
     * @param messageHandler
     *                           the message handler for the server
     * @return the running web server
     */
    static WebServer serve(final Context ctx,
            final Consumer<Session> connectHandler,
            final Consumer<String> messageHandler) {

        final Server s = configureServer(ctx,
                new WumpusWebSocketServlet(connectHandler, messageHandler));

        Mediator.require(Server::start, s);
        return () -> s;

    }

    /**
     * Configure the jetty server to serve the websocket endpoint servlet.
     *
     * @param ctx
     *                the application context to use
     * @param eps
     *                the endpoint servlet to serve
     * @return the new server instance, not yet started
     */
    private static Server configureServer(final Context ctx,
            final Servlet eps) {
        final Server s = new Server();

        final ServletContextHandler sch = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        sch.setContextPath(ctx.requiredProperty("web.server.context.path"));
        sch.addServlet(new ServletHolder(eps),
                ctx.requiredProperty("web.server.servlet.path.spec"));
        s.setHandler(sch);

        final HttpConfiguration config = new HttpConfiguration();
        config.addCustomizer(new SecureRequestCustomizer());

        final SslContextFactory sslctx = new SslContextFactory.Server();
        sslctx.setKeyStoreResource(Resource.newClassPathResource(
                ctx.requiredProperty("web.server.keystore.path")));
        sslctx.setKeyStorePassword(
                ctx.requiredProperty("web.server.keystore.password"));

        final ServerConnector wsscon = new ServerConnector(s,
                new SslConnectionFactory(sslctx,
                        HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(config));
        wsscon.setPort(
                Integer.parseInt(ctx.requiredProperty("web.server.port")));
        s.addConnector(wsscon);

        return s;
    }

    /**
     * Get the jetty server instance wrapped by this.
     *
     * @return the underlying jetty instance
     */
    Server server();

    /**
     * Stop the server or crash.
     */
    default void stop() {
        Mediator.require(Server::stop, server());
    }

    /**
     * Determine if server is running.
     *
     * @return true if in a running state, false otherwise
     */
    default boolean isRunning() {
        return server().isRunning();
    }
}
