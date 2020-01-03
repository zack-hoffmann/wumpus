package wumpus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public final class AppTest {

    /**
     * Generate a mock context instance.
     *
     * @return a mock context instance
     */
    private static Context getContext() {
        final Map<String, String> ctx = new HashMap<String, String>();
        ctx.put("foo", "bar");
        return s -> {
            return Optional.ofNullable(ctx.get(s));
        };
    }

    /**
     * App created with a given context returns that context.
     */
    @Test
    public void createWithContext() {
        final Context ctx = getContext();
        final App a = App.create(ctx);
        Assert.assertEquals(ctx.property("foo"), a.context().property("foo"));
    }

    /**
     * App runs.
     */
    @Test
    public void runs() {
        App.create(getContext()).run();
    }
}
