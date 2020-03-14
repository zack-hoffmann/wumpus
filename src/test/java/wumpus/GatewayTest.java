package wumpus;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the network gateway.
 */
public final class GatewayTest {

    @Before
    public void clearPools() {
        SessionPool.initialSessionPool.map().clear();
        SessionPool.playerSessionPool.map().clear();
    }
    
    /**
     * Accepts initial request for token.
     */
    @Test
    public void acceptsInboundInitialToken() {
        final Message tokenRequest = Message.Type.TOKEN.newTokenlessMessage();
        final Gateway gw = Gateway.create(null);
        final Mock.Session ses = Mock.Session.create();
        gw.acceptInbound(tokenRequest, ses);
        Assert.assertTrue(
                SessionPool.initialSessionPool.map().values().contains(ses));
        Assert.assertEquals(Message.Type.TOKEN, ses.sentMessage().type());
    }

    /**
     * Logins without an initial session are rejected.
     */
    @Test
    public void rejectsNonInitialLogin() {
        final Message loginRequest = Message.Type.LOGIN
                .newTokenlessMessage("ADMIN", "ADMIN");
        final Gateway gw = Gateway.create(null);
        final Mock.Session ses = Mock.Session.create();
        gw.acceptInbound(loginRequest, ses);
        Assert.assertFalse(
                SessionPool.playerSessionPool.map().values().contains(ses));
        Assert.assertEquals(Message.Type.ERROR, ses.sentMessage().type());
    }

    /**
     * Valid logins of initial sessions are accepted.
     */
    @Test
    public void acceptsInitialLogin() {
        final Mock.Session ses = Mock.Session.create();
        SessionPool.initialSessionPool.map().put("FOO_TOKEN", ses);
        final Message loginRequest = Message.Type.LOGIN.newMessage("FOO_TOKEN",
                "ADMIN", "ADMIN");
        final Gateway gw = Gateway.create(null);
        gw.acceptInbound(loginRequest);
        Assert.assertTrue(
                SessionPool.playerSessionPool.map().values().contains(ses));
        Assert.assertEquals(Message.Type.TOKEN, ses.sentMessage().type());
    }

    /**
     * Invalid logins of initial sessions are rejected.
     */
    @Test
    public void rejectsBadInitialLogin() {
        final Mock.Session ses = Mock.Session.create();
        SessionPool.initialSessionPool.map().put("FOO_TOKEN", ses);
        final Message loginRequest = Message.Type.LOGIN.newMessage("FOO_TOKEN",
                "FOO", "BAR");
        final Gateway gw = Gateway.create(null);
        gw.acceptInbound(loginRequest);
        Assert.assertFalse(
                SessionPool.playerSessionPool.map().values().contains(ses));
        Assert.assertEquals(Message.Type.ERROR, ses.sentMessage().type());
    }

    /**
     * Messages can be sent outbound.
     */
    @Test
    public void sendMessage() {
        final Mock.Session ses = Mock.Session.create();
        SessionPool.playerSessionPool.map().put("FOO_TOKEN", ses);
        final Message msg = Message.Type.COMMAND.newMessage("FOO_TOKEN", "BAR");
        Gateway.create(null).sendOutbound(msg);
        Assert.assertEquals(msg.type(), ses.sentMessage().type());
        Assert.assertEquals(msg.params()[0], ses.sentMessage().params()[0]);
    }

    /**
     * Command messages with a player session are accepted.
     */
    @Test
    public void acceptAuthenticatedCommand() {
        final Mock.Session ses = Mock.Session.create();
        final Message msg = Message.Type.COMMAND.newMessage("FOO_TOKEN", "BAR");
        final Queue<Message> msgQueue = new LinkedList<>();
        Gateway.create(msgQueue).acceptInbound(msg);
        Assert.assertTrue(msgQueue.contains(msg));
    }

    /**
     * Command messages without a player session token are rejected.
     */
    @Test
    public void rejectUnauthenticatedCommand() {
        final Mock.Session ses = Mock.Session.create();
        SessionPool.initialSessionPool.map().put("FOO_TOKEN", ses);
        final Message msg = Message.Type.COMMAND.newMessage("FOO_TOKEN", "BAR");
        final Queue<Message> msgQueue = new LinkedList<>();
        Gateway.create(msgQueue).acceptInbound(msg);
        Assert.assertFalse(msgQueue.contains(msg));
        Assert.assertEquals(Message.Type.ERROR, ses.sentMessage().type());
    }
}
