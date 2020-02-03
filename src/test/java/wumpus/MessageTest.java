package wumpus;

import org.junit.Assert;
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
            + "COMMAND" + Message.DELIM + "BAR";

    /**
     * Test tokenless raw message for parsing.
     */
    private static final String TOKENLESS_MESSAGE = Message.DELIM + "COMMAND"
            + Message.DELIM + "BAR";

    /**
     * Raw message without delimiters.
     */
    private static final String BAD_MESSAGE_1 = TEST_TOKEN + "COMMAND" + "BAR";

    /**
     * Raw message with unknown type.
     */
    private static final String BAD_MESSAGE_2 = TEST_TOKEN + Message.DELIM
            + "CMD" + Message.DELIM + "BAR";

    /**
     * Raw message without enough parts.
     */
    private static final String BAD_MESSAGE_3 = TEST_TOKEN;

    private static final Context MOCK_CTX = Mock.Context.create()
            .withProperty("string.pool.token.name", "token")
            .withProperty("string.pool.token.size", "100")
            .withProperty("string.pool.param.name", "token")
            .withProperty("string.pool.param.size", "100");

    private static final App MOCK_APP = Mock.App.create().withContext(MOCK_CTX);

    /**
     * New message has correct parts.
     */
    @Test
    public void newMessage() {
        final Message m = Message.Type.COMMAND.newMessage(TEST_TOKEN, MOCK_APP,
                TEST_PARAMS);
        Assert.assertEquals(TEST_TOKEN, m.token());
        Assert.assertEquals(Message.Type.COMMAND, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * New message is output in correct raw format.
     */
    @Test
    public void newMessageRaw() {
        final Message m = Message.Type.COMMAND.newMessage(TEST_TOKEN, MOCK_APP,
                TEST_PARAMS);
        Assert.assertEquals(PARSE_MESSAGE, m.rawString());
    }

    /**
     * New tokenless message has correct parts.
     */
    @Test
    public void newMessageNoToken() {
        final Message m = Message.Type.COMMAND.newMessage(MOCK_APP,
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
        final Message m = Message.parse(MOCK_APP, PARSE_MESSAGE);
        Assert.assertEquals(TEST_TOKEN, m.token());
        Assert.assertEquals(Message.Type.COMMAND, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * Parsed tokenless message has correct parts.
     */
    @Test
    public void parseTokenlessMessage() {
        final Message m = Message.parse(MOCK_APP, TOKENLESS_MESSAGE);
        Assert.assertEquals("", m.token());
        Assert.assertEquals(Message.Type.COMMAND, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * Raw message with no delimiters cannot be parsed.
     */
    @Test(expected = Message.ParseException.class)
    public void parseNoDelimMessage() {
        Message.parse(MOCK_APP, BAD_MESSAGE_1);
    }

    /**
     * Raw message with an invalid message type cannot be parsed.
     */
    @Test(expected = Message.ParseException.class)
    public void parseBadTypeMessage() {
        Message.parse(MOCK_APP, BAD_MESSAGE_2);
    }

    /**
     * Raw message without enough parts cannot be parsed.
     */
    @Test(expected = Message.ParseException.class)
    public void parseInsufficientMessage() {
        Message.parse(MOCK_APP, BAD_MESSAGE_3);
    }
}
