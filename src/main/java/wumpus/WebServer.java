package wumpus;

import javax.servlet.Servlet;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
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
        private final transient Endpoint ep;

        /**
         * Create new web socket servlet for the web socket endpoint.
         *
         * @param nep
         *                the new endpoint to serve
         */
        private WumpusWebSocketServlet(final Endpoint nep) {
            this.ep = nep;
        }

        @Override
        public void configure(final WebSocketServletFactory factory) {
            factory.setCreator((req, resp) -> {

                return ep;
            });
        }

    }

    /**
     * Create and run a new web socket server. Will crash if the server cannot
     * be built or started.
     *
     * @param ctx
     *                           the application context to use
     * @param messageHandler
     *                           the message handler for the server
     * @return the running web server
     */
    static WebServer serve(final Context ctx,
            final RiskyConsumer<String> messageHandler) {

        final Endpoint ep = buildEndpoint(messageHandler);
        final Server s = configureServer(ctx, new WumpusWebSocketServlet(ep));

        Mediator.require(Server::start, s);
        return () -> s;

    }

    /**
     * Build an anonymous web socket endpoint.
     *
     * @param messageHandler
     *                           the handler for the endpoint
     * @return the endpoint instance
     */
    private static Endpoint buildEndpoint(
            final RiskyConsumer<String> messageHandler) {

        final MessageHandler.Whole<String> handler = (message) -> Mediator
                .attempt(messageHandler::accept, message);

        return new Endpoint() {

            @Override
            public void onOpen(final Session session,
                    final EndpointConfig config) {
                // TODO Register session
                session.addMessageHandler(handler);

            }
            // TODO error and close handlers

        };
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
        final Server s = new Server(
                Integer.parseInt(ctx.requiredProperty("web.server.port")));

        final ServletContextHandler sch = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        sch.setContextPath(ctx.requiredProperty("web.server.context.path"));
        sch.addServlet(new ServletHolder(eps),
                ctx.requiredProperty("web.server.servlet.path.spec"));
        s.setHandler(sch);

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
