package wumpus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.SuspendToken;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;

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

        default App withContext(final wumpus.Context ctx) {
            return withRoot(wumpus.App.create(ctx).memoryRoot());
        }

        default App withRoot(final Map<String, Object> root) {
            final Map<String, Object> newRoot = new HashMap<>();
            newRoot.putAll(this.memoryRoot());
            newRoot.putAll(root);
            return () -> newRoot;
        }

        default App withAuthenticator(final Authenticator auth) {
            final Map<String, Object> newRoot = new HashMap<>();
            newRoot.put("AUTHENTICATOR", auth);
            return withRoot(newRoot);
        }
    }

    class Session implements org.eclipse.jetty.websocket.api.Session {

        public static Session create() {
            return new Session();
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
            return null;
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
            return false;
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
