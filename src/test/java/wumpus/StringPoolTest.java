package wumpus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * String pool test suite.
 *
 * Note that because this is testing memory occupancy of strings and not
 * equivalence we are using "==" here!
 */
public final class StringPoolTest {

    /**
     * Default test pool size.
     */
    private static final int SIZE = 100;

    /**
     * Test character space 1.
     */
    private static final char[] TEST_CHAR_MEM_1 = {'f', 'o', 'o'};

    /**
     * Test character space 2.
     */
    private static final char[] TEST_CHAR_MEM_2 = {'f', 'o', 'o'};

    /**
     * Test character space 3.
     */
    private static final char[] TEST_CHAR_MEM_3 = {'b', 'a', 'r'};

    /**
     * Test string over character space 1.
     */
    private static final String TEST_STR_1 = new String(TEST_CHAR_MEM_1);

    /**
     * Test string over character space 2.
     */
    private static final String TEST_STR_2 = new String(TEST_CHAR_MEM_2);

    /**
     * Test string over character space 3.
     */
    private static final String TEST_STR_3 = new String(TEST_CHAR_MEM_3);

    /**
     * Generate a mock context instance.
     *
     * @return a mock context instance
     */
    private static Context getContext() {
        final Map<String, String> ctx = new HashMap<String, String>();
        ctx.put("string.pool.token.name", "token");
        ctx.put("string.pool.token.size", "100");
        return s -> {
            return Optional.ofNullable(ctx.get(s));
        };
    }

    /**
     * Empty the static pools before each test.
     */
    @Before
    public void clearPools() {
        StringPool.abandon();
    }

    /**
     * An interned string will be reused when a future equivalent string is
     * interned.
     */
    @Test
    public void intern() {
        final StringPool pool = StringPool.recall("", SIZE);
        pool.intern(TEST_STR_1);
        Assert.assertFalse(TEST_STR_2 == pool.intern(TEST_STR_2));
        Assert.assertTrue(TEST_STR_1 == pool.intern(TEST_STR_2));
    }

    /**
     * An interned string will not be re-used once it has been expire from the
     * pool.
     */
    @Test
    public void expire() {
        final StringPool pool = StringPool.recall("", 1);
        pool.intern(TEST_STR_1);
        pool.intern(TEST_STR_3);
        Assert.assertFalse(TEST_STR_1 == pool.intern(TEST_STR_2));
    }

    /**
     * The same pool with the same interned strings can be re-used by name.
     */
    @Test
    public void recall() {
        StringPool.recall("", SIZE).intern(TEST_STR_1);
        final String t = StringPool.recall("", SIZE).intern(TEST_STR_2);
        Assert.assertTrue(t == TEST_STR_1);
    }

    /**
     * Strings are not interned across pools with different names.
     */
    @Test
    public void doNotRecall() {
        StringPool.recall("", SIZE).intern(TEST_STR_1);
        final String t = StringPool.recall("1", SIZE).intern(TEST_STR_2);
        Assert.assertFalse(t == TEST_STR_1);
    }

    /**
     * Abandoned pools should not return previously interned strings.
     */
    @Test
    public void abandon() {
        StringPool.recall("", SIZE).intern(TEST_STR_1);
        StringPool.abandon();
        final String t = StringPool.recall("", SIZE).intern(TEST_STR_2);
        Assert.assertFalse(t == TEST_STR_1);
    }

    /**
     * Generate a token that is not equal to another token.
     */
    @Test
    public void token() {
        final StringPool pool = StringPool.recall("", SIZE);
        final String token = pool.newToken();
        Assert.assertNotNull(token);
        Assert.assertNotEquals(token, pool.newToken());
    }

    /**
     * Recall from dedicated token pool.
     */
    @Test
    public void tokenPool() {
        final StringPool pool = StringPool.tokenPool(getContext());
        final String token = pool.newToken();
        final String tokenDup = new String(token.toCharArray());
        Assert.assertFalse(token == tokenDup);
        Assert.assertTrue(token == pool.intern(tokenDup));
    }
}
