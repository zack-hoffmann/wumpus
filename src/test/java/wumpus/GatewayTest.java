package wumpus;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for the network gateway.
 */
public final class GatewayTest {

    /**
     * Configuration context.
     */
    private static final Context MOCK_CTX = Mock.Context.create()
            .withProperty("string.pool.token.name", "token")
            .withProperty("string.pool.token.size", "100")
            .withProperty("string.pool.param.name", "token")
            .withProperty("string.pool.param.size", "100");

    /**
     * Accepts initial request for token.
     */
    @Test
    public void acceptsInboundInitialToken() {
        final App app = Mock.App.create().withContext(MOCK_CTX);
        final Message tokenRequest = Message.Type.TOKEN.newMessage(app);
        final Gateway gw = Gateway.create(app, null);
        final Mock.Session ses = Mock.Session.create();
        gw.acceptInbound(tokenRequest, ses);
        Assert.assertTrue(
                app.initialSessionPool().map().values().contains(ses));
        Assert.assertEquals(Message.Type.TOKEN, ses.sentMessage(app).type());
    }

    /**
     * Logins without an initial session are rejected.
     */
    @Test
    public void rejectsNonInitialLogin() {
        final App app = Mock.App.create().withContext(MOCK_CTX);
        final Message loginRequest = Message.Type.LOGIN.newMessage(app, "ADMIN",
                "ADMIN");
        final Gateway gw = Gateway.create(app, null);
        final Mock.Session ses = Mock.Session.create();
        gw.acceptInbound(loginRequest, ses);
        Assert.assertFalse(
                app.playerSessionPool().map().values().contains(ses));
        Assert.assertEquals(Message.Type.ERROR, ses.sentMessage(app).type());
    }

    /**
     * Valid logins of initial sessions are accepted.
     */
    @Test
    public void acceptsInitialLogin() {
        final Mock.Session ses = Mock.Session.create();
        final App app = Mock.App.create().withContext(MOCK_CTX)
                .withInitialSession("FOO_TOKEN", ses);
        final Message loginRequest = Message.Type.LOGIN.newMessage("FOO_TOKEN",
                app, "ADMIN", "ADMIN");
        final Gateway gw = Gateway.create(app, null);
        gw.acceptInbound(loginRequest);
        Assert.assertTrue(app.playerSessionPool().map().values().contains(ses));
        Assert.assertEquals(Message.Type.TOKEN, ses.sentMessage(app).type());
    }

    /**
     * Invalid logins of initial sessions are rejected.
     */
    @Test
    public void rejectsBadInitialLogin() {
        final Mock.Session ses = Mock.Session.create();
        final App app = Mock.App.create().withContext(MOCK_CTX)
                .withInitialSession("FOO_TOKEN", ses);
        final Message loginRequest = Message.Type.LOGIN.newMessage("FOO_TOKEN",
                app, "FOO", "BAR");
        final Gateway gw = Gateway.create(app, null);
        gw.acceptInbound(loginRequest);
        Assert.assertFalse(
                app.playerSessionPool().map().values().contains(ses));
        Assert.assertEquals(Message.Type.ERROR, ses.sentMessage(app).type());
    }

    /**
     * Messages can be sent outbound.
     */
    @Test
    public void sendMessage() {
        final Mock.Session ses = Mock.Session.create();
        final App app = Mock.App.create().withContext(MOCK_CTX)
                .withInitialSession("FOO_TOKEN", ses);
        final Message msg = Message.Type.COMMAND.newMessage("FOO_TOKEN", app,
                "BAR");
        Gateway.create(app, null).sendOutbound(msg);
        Assert.assertEquals(msg.type(), ses.sentMessage(app).type());
        Assert.assertEquals(msg.params()[0], ses.sentMessage(app).params()[0]);
    }

    /**
     * Command messages with a player session are accepted.
     */
    @Test
    public void acceptAuthenticatedCommand() {
        final Mock.Session ses = Mock.Session.create();
        final App app = Mock.App.create().withContext(MOCK_CTX)
                .withPlayerSession("FOO_TOKEN", ses);
        final Message msg = Message.Type.COMMAND.newMessage("FOO_TOKEN", app,
                "BAR");
        final Queue<Message> msgQueue = new LinkedList<>();
        Gateway.create(app, msgQueue).acceptInbound(msg);
        Assert.assertTrue(msgQueue.contains(msg));
    }

    /**
     * Command messages without a player session token are rejected.
     */
    @Test
    public void rejectUnauthenticatedCommand() {
        final Mock.Session ses = Mock.Session.create();
        final App app = Mock.App.create().withContext(MOCK_CTX)
                .withInitialSession("FOO_TOKEN", ses);
        final Message msg = Message.Type.COMMAND.newMessage("FOO_TOKEN", app,
                "BAR");
        final Queue<Message> msgQueue = new LinkedList<>();
        Gateway.create(app, msgQueue).acceptInbound(msg);
        Assert.assertFalse(msgQueue.contains(msg));
        Assert.assertEquals(Message.Type.ERROR, ses.sentMessage(app).type());
    }
}
