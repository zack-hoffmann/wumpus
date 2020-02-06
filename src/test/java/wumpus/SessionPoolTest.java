package wumpus;

import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public final class SessionPoolTest {

    private static final Context MOCK_CTX = Mock.Context.create()
            .withProperty("string.pool.token.name", "token")
            .withProperty("string.pool.token.size", "100");

    private static final App MOCK_APP = Mock.App.create().withContext(MOCK_CTX);
    private static final Session MOCK_SESSION = Mock.Session.create();

    @Test
    public void create() {
        Assert.assertNotNull(SessionPool.create(MOCK_APP));
    }

    @Test
    public void emptyMap() {
        final Map<String, Session> map = SessionPool.create(MOCK_APP).map();
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void bindNothing() {
        final SessionPool pool = SessionPool.create(MOCK_APP);
        Assert.assertEquals(pool.map(), pool.bind(Optional.empty()));
    }

    @Test
    public void bindSomething() {
        final Map<String, Session> entry = SessionPool.create(MOCK_APP)
                .bind(Optional.of(MOCK_SESSION));
        Assert.assertEquals(1, entry.entrySet().size());
        Assert.assertEquals(MOCK_SESSION, entry.values().iterator().next());
        Assert.assertNotNull(entry.keySet().iterator().next());
    }

    @Test
    public void register() {
        final String token = SessionPool.create(MOCK_APP)
                .register(MOCK_SESSION);
        Assert.assertNotNull(token);
    }

    @Test
    public void renewBadToken() {
        Assert.assertTrue(SessionPool.create(MOCK_APP).renew("").isEmpty());
    }

    @Test
    public void renew() {
        final SessionPool pool = SessionPool.create(MOCK_APP);
        final String t1 = pool.register(MOCK_SESSION);
        Assert.assertNotEquals(t1, pool.renew(t1).get());
    }

    @Test
    public void recall() {
        final SessionPool pool = SessionPool.create(MOCK_APP);
        final String t1 = pool.register(MOCK_SESSION);
        Assert.assertTrue(pool.session(t1).isPresent());
    }

    @Test
    public void badRecall() {
        final SessionPool pool = SessionPool.create(MOCK_APP);
        Assert.assertTrue(pool.session("").isEmpty());
    }

    @Test
    public void noRecallAfterClean() {
        final SessionPool pool = SessionPool.create(MOCK_APP);
        final String t1 = pool.register(MOCK_SESSION);
        pool.clean();
        Assert.assertTrue(pool.session(t1).isEmpty());
    }
}
