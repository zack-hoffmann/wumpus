package wumpus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
    interface Application extends wumpus.Application {

        /**
         * Create an empty mock context.
         *
         * @return empty context
         */
        static Application create() {
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
        default Application withProperty(final String p, final String v) {
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
