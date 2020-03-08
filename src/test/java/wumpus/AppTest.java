package wumpus;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for the app instance.
 *
 * TODO Many app methods are not yet tested.
 */
public final class AppTest {

    /**
     * Configuration context.
     */
    private static final Context MOCK_CTX = Mock.Context.create()
            .withProperty("foo", "bar");

    /**
     * App created with a given context returns that context.
     */
    @Test
    public void createWithContext() {
        final Context ctx = MOCK_CTX;
        final App a = App.create(ctx);
        Assert.assertEquals(ctx.property("foo"), a.context().property("foo"));
    }

}
