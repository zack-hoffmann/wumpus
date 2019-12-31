package wumpus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for messages.
 */
public final class MessageTest {

    /**
     * Test sender identity token.
     */
    private static final String TEST_TOKEN = "FOO";

    /**
     * Test message parameters.
     */
    private static final String[] TEST_PARAMS = new String[] {"BAR"};

    /**
     * Test raw message for parsing.
     */
    private static final String PARSE_MESSAGE = TEST_TOKEN + Message.DELIM
            + "PROMPT" + Message.DELIM + "BAR";

    /**
     * Test tokenless raw message for parsing.
     */
    private static final String TOKENLESS_MESSAGE = Message.DELIM + "PROMPT"
            + Message.DELIM + "BAR";

    /**
     * Raw message without delimiters.
     */
    private static final String BAD_MESSAGE_1 = TEST_TOKEN + "PROMPT" + "BAR";

    /**
     * Raw message with unknown type.
     */
    private static final String BAD_MESSAGE_2 = TEST_TOKEN + Message.DELIM
            + "CMD" + Message.DELIM + "BAR";

    /**
     * Raw message without enough parts.
     */
    private static final String BAD_MESSAGE_3 = TEST_TOKEN;

    /**
     * Generate a mock context instance.
     *
     * @return a mock context instance
     */
    private static Context getContext() {
        final Map<String, String> ctx = new HashMap<String, String>();
        ctx.put("string.pool.token.name", "token");
        ctx.put("string.pool.token.size", "100");
        ctx.put("string.pool.param.name", "token");
        ctx.put("string.pool.param.size", "100");
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
     * New message has correct parts.
     */
    @Test
    public void newMessage() {
        final Message m = Message.Type.COMMAND.newMessage(TEST_TOKEN,
                getContext(), TEST_PARAMS);
        Assert.assertEquals(TEST_TOKEN, m.token());
        Assert.assertEquals(Message.Type.COMMAND, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * New message is output in correct raw format.
     */
    @Test
    public void newMessageRaw() {
        final Message m = Message.Type.PROMPT.newMessage(TEST_TOKEN,
                getContext(), TEST_PARAMS);
        Assert.assertEquals(PARSE_MESSAGE, m.rawString());
    }

    /**
     * New tokenless message has correct parts.
     */
    @Test
    public void newMessageNoToken() {
        final Message m = Message.Type.COMMAND.newMessage(getContext(),
                TEST_PARAMS);
        Assert.assertEquals("", m.token());
        Assert.assertEquals(Message.Type.COMMAND, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * Parsed message has correct parts.
     */
    @Test
    public void parseMessage() {
        final Message m = Message.parse(getContext(), PARSE_MESSAGE);
        Assert.assertEquals(TEST_TOKEN, m.token());
        Assert.assertEquals(Message.Type.PROMPT, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * Parsed tokenless message has correct parts.
     */
    @Test
    public void parseTokenlessMessage() {
        final Message m = Message.parse(getContext(), TOKENLESS_MESSAGE);
        Assert.assertEquals("", m.token());
        Assert.assertEquals(Message.Type.PROMPT, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * Raw message with no delimiters cannot be parsed.
     */
    @Test(expected = Message.ParseException.class)
    public void parseNoDelimMessage() {
        Message.parse(getContext(), BAD_MESSAGE_1);
    }

    /**
     * Raw message with an invalid message type cannot be parsed.
     */
    @Test(expected = Message.ParseException.class)
    public void parseBadTypeMessage() {
        Message.parse(getContext(), BAD_MESSAGE_2);
    }

    /**
     * Raw message without enough parts cannot be parsed.
     */
    @Test(expected = Message.ParseException.class)
    public void parseInsufficientMessage() {
        Message.parse(getContext(), BAD_MESSAGE_3);
    }
}
