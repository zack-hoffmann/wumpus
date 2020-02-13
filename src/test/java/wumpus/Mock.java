package wumpus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.BatchMode;
import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.SuspendToken;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.WriteCallback;

public interface Mock {

    /**
     * Mock of the context.
     */
    interface Context extends wumpus.Context {

        /**
         * Create an empty mock context.
         *
         * @return empty context
         */
        static Context create() {
            return s -> Optional.empty();
        }

        /**
         * Extend the context with an additional property.
         *
         * @param p
         *              the new property name
         * @param v
         *              the new property value
         * @return the extended context
         */
        default Context withProperty(final String p, final String v) {
            return s -> {
                if (s.equals(p)) {
                    return Optional.of(v);
                } else {
                    return this.property(s);
                }
            };
        }
    }

    /**
     * Mock of the app.
     */
    interface App extends wumpus.App {

        /**
         * Create an empty mock app.
         *
         * @return new app instance
         */
        static App create() {
            return () -> Collections.emptyMap();
        }

        /**
         * Seed the app with an established initial session.
         *
         * @param token
         *                    the token of the initial session
         * @param session
         *                    the session of the initial session
         * @return the extended app instance
         */
        default App withInitialSession(final String token,
                final wumpus.Session session) {
            final SessionPool pool = SessionPool.create(this);
            pool.map().put(token, session);
            final Map<String, Object> newRoot = new HashMap<>();
            newRoot.put("INITIAL_SESSION_POOL", pool);
            return withRoot(newRoot);
        }

        /**
         * Seed the app with an established, authenticated player session.
         *
         * @param token
         *                    the token of the player session
         * @param session
         *                    the session of the player session
         * @return the extended app instance
         */
        default App withPlayerSession(final String token,
                final wumpus.Session session) {
            final SessionPool pool = SessionPool.create(this);
            pool.map().put(token, session);
            final Map<String, Object> newRoot = new HashMap<>();
            newRoot.put("PLAYER_SESSION_POOL", pool);
            this.authenticator().authenticate(token, "ADMIN", "ADMIN");
            return withRoot(newRoot);
        }

        /**
         * Extend the app with additional context.
         *
         * @param ctx
         *                the additional context
         * @return the extended app instance
         */
        default App withContext(final wumpus.Context ctx) {
            return withRoot(wumpus.App.create(ctx).memoryRoot());
        }

        /**
         * Extend the app with additional root memory, overriding conflicting
         * values.
         *
         * @param root
         *                 the root memory map to lay over the existing one
         * @return the extended app instance
         */
        default App withRoot(final Map<String, Object> root) {
            final Map<String, Object> newRoot = new HashMap<>();
            newRoot.putAll(this.memoryRoot());
            newRoot.putAll(root);
            return () -> newRoot;
        }

    }

    /**
     * Mock wrapper session.
     */
    interface Session extends wumpus.Session {
        /**
         * Create open mock session.
         *
         * @return open mock session
         */
        static Session create() {
            final JettySession js = JettySession.createOpen();
            return () -> js;
        }

        /**
         * Create closed mock session.
         *
         * @return closed mock session
         */
        static Session createClosed() {
            final JettySession js = JettySession.createClosed();
            return () -> js;
        }

        /**
         * Access the last message sent via this session.
         *
         * @param app
         *                the application
         * @return the last message sent
         */
        default Message sentMessage(final wumpus.App app) {
            return Message.parse(app,
                    ((Mock.JettySession) unwrap()).sentString());
        }
    }

    /**
     * Mock native Jetty session for testing the wrapper session and things that
     * use it.
     */
    class JettySession implements org.eclipse.jetty.websocket.api.Session {

        /**
         * Create an open mock native session.
         *
         * @return an open mock native session.
         */
        public static JettySession createOpen() {
            return new JettySession(true);
        }

        /**
         * Create a closed mock native session.
         *
         * @return a closed mock native session.
         */
        public static JettySession createClosed() {
            return new JettySession(false);
        }

        /**
         * If the session is open.
         */
        private final boolean open;

        /**
         * The last string sent via this session.
         */
        private String sentString;

        /**
         * Instantiate mock session with openness status.
         *
         * @param o
         *              true if session should be presented as open, false if
         *              otherwise
         */
        private JettySession(final boolean o) {
            open = o;
        }

        /**
         * Access the last string which was sent via this session.
         *
         * @return the last string sent
         */
        public String sentString() {
            return sentString;
        }

        @Override
        public void close() {
        }

        @Override
        public void close(final CloseStatus closeStatus) {
        }

        @Override
        public void close(final int statusCode, final String reason) {
        }

        @Override
        public void disconnect() throws IOException {
        }

        @Override
        public long getIdleTimeout() {
            return 0;
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return null;
        }

        @Override
        public WebSocketPolicy getPolicy() {
            return null;
        }

        @Override
        public String getProtocolVersion() {
            return null;
        }

        @Override
        public RemoteEndpoint getRemote() {
            return new RemoteEndpoint() {

                @Override
                public void sendBytes(final ByteBuffer data)
                        throws IOException {

                }

                @Override
                public Future<Void> sendBytesByFuture(final ByteBuffer data) {
                    return null;
                }

                @Override
                public void sendBytes(final ByteBuffer data,
                        final WriteCallback callback) {

                }

                @Override
                public void sendPartialBytes(final ByteBuffer fragment,
                        final boolean isLast) throws IOException {

                }

                @Override
                public void sendPartialString(final String fragment,
                        final boolean isLast) throws IOException {

                }

                @Override
                public void sendPing(final ByteBuffer applicationData)
                        throws IOException {

                }

                @Override
                public void sendPong(final ByteBuffer applicationData)
                        throws IOException {

                }

                @Override
                public void sendString(final String text) throws IOException {
                    if (open) {
                        sentString = text;
                    } else {
                        throw new IOException();
                    }
                }

                @Override
                public Future<Void> sendStringByFuture(final String text) {
                    return null;
                }

                @Override
                public void sendString(final String text,
                        final WriteCallback callback) {

                }

                @Override
                public BatchMode getBatchMode() {
                    return null;
                }

                @Override
                public void setBatchMode(final BatchMode mode) {

                }

                @Override
                public InetSocketAddress getInetSocketAddress() {
                    return null;
                }

                @Override
                public void flush() throws IOException {

                }

            };
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public UpgradeRequest getUpgradeRequest() {
            return null;
        }

        @Override
        public UpgradeResponse getUpgradeResponse() {
            return null;
        }

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public void setIdleTimeout(final long ms) {
        }

        @Override
        public SuspendToken suspend() {
            return null;
        }

    }
}
