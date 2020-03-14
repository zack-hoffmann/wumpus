package wumpus;

import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public final class SessionPoolTest {

    /**
     * Mock open session.
     */
    private static final Session MOCK_SESSION = Mock.Session.create();

    /**
     * Mock closed session.
     */
    private static final Session MOCK_CLOSED_SESSION = Mock.Session
            .createClosed();

    /**
     * Pool creates successfully.
     */
    @Test
    public void create() {
        Assert.assertNotNull(SessionPool.create());
    }

    /**
     * Initial pool map is empty.
     */
    @Test
    public void emptyMap() {
        final Map<String, Session> map = SessionPool.create().map();
        Assert.assertTrue(map.isEmpty());
    }

    /**
     * Pool does not bind lack of session.
     */
    @Test
    public void bindNothing() {
        final SessionPool pool = SessionPool.create();
        Assert.assertEquals(pool.map(), pool.bind(Optional.empty()));
    }

    /**
     * Pool successfully binds session.
     */
    @Test
    public void bindSomething() {
        final Map<String, Session> entry = SessionPool.create()
                .bind(Optional.of(MOCK_SESSION));
        Assert.assertEquals(1, entry.entrySet().size());
        Assert.assertEquals(MOCK_SESSION, entry.values().iterator().next());
        Assert.assertNotNull(entry.keySet().iterator().next());
    }

    /**
     * Pool returns valid token when registering session.
     */
    @Test
    public void register() {
        final String token = SessionPool.create().register(MOCK_SESSION);
        Assert.assertNotNull(token);
    }

    /**
     * Pool returns no result when attempting to renew nonexistent token.
     */
    @Test
    public void renewBadToken() {
        Assert.assertTrue(SessionPool.create().renew("").isEmpty());
    }

    /**
     * Pool issues new, different token for session when renewed.
     */
    @Test
    public void renew() {
        final SessionPool pool = SessionPool.create();
        final String t1 = pool.register(MOCK_SESSION);
        Assert.assertNotEquals(t1, pool.renew(t1).get());
    }

    /**
     * Pool returns session when token provided.
     */
    @Test
    public void recall() {
        final SessionPool pool = SessionPool.create();
        final String t1 = pool.register(MOCK_SESSION);
        Assert.assertTrue(pool.session(t1).isPresent());
    }

    /**
     * Pool does not return session when invalid token provided.
     */
    @Test
    public void badRecall() {
        final SessionPool pool = SessionPool.create();
        Assert.assertTrue(pool.session("").isEmpty());
    }

    /**
     * Pool containing closed session does not return that session after clean.
     */
    @Test
    public void noRecallAfterClean() {
        final SessionPool pool = SessionPool.create();
        final String t1 = pool.register(MOCK_CLOSED_SESSION);
        pool.clean();
        Assert.assertTrue(pool.session(t1).isEmpty());
    }
}
