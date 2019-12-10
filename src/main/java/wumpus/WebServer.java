package wumpus;

import java.util.function.Consumer;

import javax.servlet.Servlet;
import javax.websocket.Endpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

@FunctionalInterface
public interface WebServer {

    static WebServer serve(final Context ctx,
            final RiskyConsumer<String> messageHandler,
            final Consumer<Session> openHandler,
            final Consumer<Session> closeHandler,
            final Consumer<Session> errorHandler) {

        final Servlet ep = buildEndpointServlet();
        final Server s = configureServer(ctx, ep);

        Mediator.require(ss -> s.start(), s);
        return () -> Mediator.require(ss -> ss.stop(), s);

    }

    private static Servlet buildEndpointServlet() {
        return null;
    }

    private static Server configureServer(final Context ctx, final Servlet ep) {
        final Server s = new Server(
                Integer.parseInt(ctx.requiredProperty("web.server.port")));

        final ServletContextHandler sch = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        sch.setContextPath(ctx.requiredProperty("web.server.context.path"));
        sch.addServlet(new ServletHolder(ep),
                ctx.requiredProperty("web.server.servlet.path.spec"));
        s.setHandler(sch);

        return s;
    }

    void stop();
}