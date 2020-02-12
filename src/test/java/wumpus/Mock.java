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

    interface Context extends wumpus.Context {

        public static Context create() {
            return s -> Optional.empty();
        }

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

    interface App extends wumpus.App {

        public static App create() {
            return () -> Collections.emptyMap();
        }

        default App withInitialSession(final String token,
                final wumpus.Session session) {
            final SessionPool pool = SessionPool.create(this);
            pool.map().put(token, session);
            final Map<String, Object> newRoot = new HashMap<>();
            newRoot.put("INITIAL_SESSION_POOL", pool);
            return withRoot(newRoot);
        }

        default App withPlayerSession(final String token,
                final wumpus.Session session) {
            final SessionPool pool = SessionPool.create(this);
            pool.map().put(token, session);
            final Map<String, Object> newRoot = new HashMap<>();
            newRoot.put("PLAYER_SESSION_POOL", pool);
            this.authenticator().authenticate(token, "ADMIN", "ADMIN");
            return withRoot(newRoot);
        }

        default App withContext(final wumpus.Context ctx) {
            return withRoot(wumpus.App.create(ctx).memoryRoot());
        }

        default App withRoot(final Map<String, Object> root) {
            final Map<String, Object> newRoot = new HashMap<>();
            newRoot.putAll(this.memoryRoot());
            newRoot.putAll(root);
            return () -> newRoot;
        }

    }

    interface Session extends wumpus.Session {
        public static Session create() {
            final JettySession js = JettySession.createOpen();
            return () -> js;
        }

        default Message sentMessage(final wumpus.App app) {
            return Message.parse(app,
                    ((Mock.JettySession) unwrap()).sentString());
        }
    }

    class JettySession implements org.eclipse.jetty.websocket.api.Session {

        public static JettySession createOpen() {
            return new JettySession(true);
        }

        public static JettySession createClosed() {
            return new JettySession(false);
        }

        private final boolean open;
        private String sentString;

        private JettySession(final boolean o) {
            open = o;
        }

        public String sentString() {
            return sentString;
        }

        @Override
        public void close() {
        }

        @Override
        public void close(CloseStatus closeStatus) {
        }

        @Override
        public void close(int statusCode, String reason) {
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
                public void sendBytes(ByteBuffer data) throws IOException {

                }

                @Override
                public Future<Void> sendBytesByFuture(ByteBuffer data) {
                    return null;
                }

                @Override
                public void sendBytes(ByteBuffer data, WriteCallback callback) {

                }

                @Override
                public void sendPartialBytes(ByteBuffer fragment,
                        boolean isLast) throws IOException {

                }

                @Override
                public void sendPartialString(String fragment, boolean isLast)
                        throws IOException {

                }

                @Override
                public void sendPing(ByteBuffer applicationData)
                        throws IOException {

                }

                @Override
                public void sendPong(ByteBuffer applicationData)
                        throws IOException {

                }

                @Override
                public void sendString(String text) throws IOException {
                    if (open) {
                        sentString = text;
                    } else {
                        throw new IOException();
                    }
                }

                @Override
                public Future<Void> sendStringByFuture(String text) {
                    return null;
                }

                @Override
                public void sendString(String text, WriteCallback callback) {

                }

                @Override
                public BatchMode getBatchMode() {
                    return null;
                }

                @Override
                public void setBatchMode(BatchMode mode) {

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
        public void setIdleTimeout(long ms) {
        }

        @Override
        public SuspendToken suspend() {
            return null;
        }

    }
}
