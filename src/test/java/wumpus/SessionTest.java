package wumpus;

import org.junit.Assert;
import org.junit.Test;

public final class SessionTest {

    @Test
    public void unwraps() {
        final Mock.JettySession jettySession = Mock.JettySession.createOpen();
        final Session ses = Session.create(jettySession);
        Assert.assertEquals(jettySession, ses.unwrap());
    }

    @Test
    public void checkOpen() {
        final Mock.JettySession jettySession = Mock.JettySession.createOpen();
        final Session ses = Session.create(jettySession);
        Assert.assertTrue(ses.isOpen());
    }

    @Test
    public void checkClosed() {
        final Mock.JettySession jettySession = Mock.JettySession.createClosed();
        final Session ses = Session.create(jettySession);
        Assert.assertFalse(ses.isOpen());
    }

    @Test
    public void writesWhenOpen() {
        final Mock.JettySession jettySession = Mock.JettySession.createOpen();
        Session.create(jettySession).send("FOO");
        Assert.assertEquals("FOO", jettySession.sentString());
    }

    @Test(expected = Session.SessionException.class)
    public void doesNotWriteWhenClosed() {
        final Mock.JettySession jettySession = Mock.JettySession.createClosed();
        Session.create(jettySession).send("");
    }
}