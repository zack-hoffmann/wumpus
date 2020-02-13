package wumpus;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for the session wrapper.
 */
public final class SessionTest {

    /**
     * Exposes the native session.
     */
    @Test
    public void unwraps() {
        final Mock.JettySession jettySession = Mock.JettySession.createOpen();
        final Session ses = Session.create(jettySession);
        Assert.assertEquals(jettySession, ses.unwrap());
    }

    /**
     * Determines when session is open.
     */
    @Test
    public void checkOpen() {
        final Mock.JettySession jettySession = Mock.JettySession.createOpen();
        final Session ses = Session.create(jettySession);
        Assert.assertTrue(ses.isOpen());
    }

    /**
     * Determines when session is closed.
     */
    @Test
    public void checkClosed() {
        final Mock.JettySession jettySession = Mock.JettySession.createClosed();
        final Session ses = Session.create(jettySession);
        Assert.assertFalse(ses.isOpen());
    }

    /**
     * Writes string to remote.
     */
    @Test
    public void writesWhenOpen() {
        final Mock.JettySession jettySession = Mock.JettySession.createOpen();
        Session.create(jettySession).send("FOO");
        Assert.assertEquals("FOO", jettySession.sentString());
    }

    /**
     * Throws exception when writing to closed session.
     */
    @Test(expected = Session.SessionException.class)
    public void doesNotWriteWhenClosed() {
        final Mock.JettySession jettySession = Mock.JettySession.createClosed();
        Session.create(jettySession).send("");
    }
}
