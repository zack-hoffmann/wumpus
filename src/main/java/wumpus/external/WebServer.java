package wumpus.external;

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

import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.function.Function;

import wumpus.Application;
import wumpus.Entity;
import wumpus.component.Message;
import wumpus.component.RemoteTerminal;

public interface WebServer {

    Function<Session, Consumer<Message>> createHandler = s -> m -> Mediator
            .attempt(n -> s.getRemote().sendString(n), m.rawString());

    Consumer<Session> registerSessionEntity = s -> Entity.create.get()
            .registerComponent((RemoteTerminal) () -> createHandler.apply(s));

    Consumer<String> registerMessageEntity = s -> Entity.create.get()
            .registerComponent(Message.parse(s));

    Supplier<WebSocketAdapter> createAdapter = () -> new WebSocketAdapter() {
        @Override
        public void onWebSocketConnect(final Session s) {
            registerSessionEntity.accept(s);
        }

        @Override
        public void onWebSocketText(final String m) {
            registerMessageEntity.accept(m);
        }
    };

    Supplier<WebSocketServlet> createServlet = () -> new WebSocketServlet() {
        private static final long serialVersionUID = -5706182301419317223L;

        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.setCreator((r, s) -> createAdapter.get());
        }
    };

    Supplier<WebServer> create = () -> {
        final Application app = Application.instance;
        final Server s = new Server();

        final ServletContextHandler sch = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        sch.setContextPath(app.requiredProperty("web.server.context.path"));
        sch.addServlet(new ServletHolder(createServlet.get()),
                app.requiredProperty("web.server.servlet.path.spec"));
        s.setHandler(sch);

        final HttpConfiguration config = new HttpConfiguration();
        config.addCustomizer(new SecureRequestCustomizer());

        final SslContextFactory sslctx = new SslContextFactory.Server();
        sslctx.setKeyStoreResource(Resource.newClassPathResource(
                app.requiredProperty("web.server.keystore.path")));
        sslctx.setKeyStorePassword(
                app.requiredProperty("web.server.keystore.password"));

        final ServerConnector wsscon = new ServerConnector(s,
                new SslConnectionFactory(sslctx,
                        HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(config));
        wsscon.setPort(
                Integer.parseInt(app.requiredProperty("web.server.port")));
        s.addConnector(wsscon);

        return () -> s;
    };

    Server server();

    default void start() {
        Mediator.require(Server::start, server());
    }

    default void stop() {
        Mediator.require(Server::stop, server());
    }

    default boolean isRunning() {
        return server().isRunning();
    }
}