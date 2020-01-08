package wumpus;

import org.junit.Assert;
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

    private static final Context MOCK_CTX = Mock.Context.create()
            .withProperty("string.pool.token.name", "token")
            .withProperty("string.pool.token.size", "100");

    private static final App MOCK_APP = Mock.App.create().withContext(MOCK_CTX);

    /**
     * An interned string will be reused when a future equivalent string is
     * interned.
     */
    @Test
    public void intern() {
        final StringPool pool = StringPool.construct(SIZE);
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
        final StringPool pool = StringPool.construct(1);
        pool.intern(TEST_STR_1);
        pool.intern(TEST_STR_3);
        Assert.assertFalse(TEST_STR_1 == pool.intern(TEST_STR_2));
    }

    /**
     * Generate a token that is not equal to another token.
     */
    @Test
    public void token() {
        final StringPool pool = StringPool.construct(SIZE);
        final String token = pool.newToken();
        Assert.assertNotNull(token);
        Assert.assertNotEquals(token, pool.newToken());
    }

    /**
     * Recall from dedicated token pool.
     */
    @Test
    public void tokenPool() {
        final StringPool pool = MOCK_APP.tokenPool();
        final String token = pool.newToken();
        final String tokenDup = new String(token.toCharArray());
        Assert.assertFalse(token == tokenDup);
        Assert.assertTrue(token == pool.intern(tokenDup));
    }
}
