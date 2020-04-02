package wumpus;

import org.junit.Assert;
import org.junit.Test;

import wumpus.component.Message;

/**
 * Test suite for messages.
 */
public final class MessageTest {

    /**
     * Test sender identity token.
     */
    private static final String TEST_TOKEN_STR = "FOO";

    private static final Token TEST_TOKEN = Token.of.apply(TEST_TOKEN_STR);

    /**
     * Test message parameters.
     */
    private static final String[] TEST_PARAMS = new String[] {"BAR"};

    /**
     * Test raw message for parsing.
     */
    private static final String PARSE_MESSAGE = TEST_TOKEN_STR + Message.DELIM
            + "COMMAND" + Message.DELIM + "BAR";

    /**
     * Test tokenless raw message for parsing.
     */
    private static final String TOKENLESS_MESSAGE = Message.DELIM + "COMMAND"
            + Message.DELIM + "BAR";

    /**
     * Raw message without delimiters.
     */
    private static final String BAD_MESSAGE_1 = TEST_TOKEN_STR + "COMMAND"
            + "BAR";

    /**
     * Raw message with unknown type.
     */
    private static final String BAD_MESSAGE_2 = TEST_TOKEN_STR + Message.DELIM
            + "CMD" + Message.DELIM + "BAR";

    /**
     * Raw message without enough parts.
     */
    private static final String BAD_MESSAGE_3 = TEST_TOKEN_STR;

    /**
     * New message has correct parts.
     */
    @Test
    public void newMessage() {
        final Message m = Message.Type.COMMAND.newMessage(TEST_TOKEN,
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
        final Message m = Message.Type.COMMAND.newMessage(TEST_TOKEN,
                TEST_PARAMS);
        Assert.assertEquals(PARSE_MESSAGE, m.rawString());
    }

    /**
     * New tokenless message has correct parts.
     */
    @Test
    public void newMessageNoToken() {
        final Message m = Message.Type.COMMAND.newMessage(TEST_PARAMS);
        Assert.assertEquals("", m.token());
        Assert.assertEquals(Message.Type.COMMAND, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * Parsed message has correct parts.
     */
    @Test
    public void parseMessage() {
        final Message m = Message.parse(PARSE_MESSAGE);
        Assert.assertEquals(TEST_TOKEN, m.token());
        Assert.assertEquals(Message.Type.COMMAND, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * Parsed tokenless message has correct parts.
     */
    @Test
    public void parseTokenlessMessage() {
        final Message m = Message.parse(TOKENLESS_MESSAGE);
        Assert.assertEquals("", m.token());
        Assert.assertEquals(Message.Type.COMMAND, m.type());
        Assert.assertEquals(TEST_PARAMS[0], m.params()[0]);
    }

    /**
     * Raw message with no delimiters cannot be parsed.
     */
    @Test(expected = Message.ParseException.class)
    public void parseNoDelimMessage() {
        Message.parse(BAD_MESSAGE_1);
    }

    /**
     * Raw message with an invalid message type cannot be parsed.
     */
    @Test(expected = Message.ParseException.class)
    public void parseBadTypeMessage() {
        Message.parse(BAD_MESSAGE_2);
    }

    /**
     * Raw message without enough parts cannot be parsed.
     */
    @Test(expected = Message.ParseException.class)
    public void parseInsufficientMessage() {
        Message.parse(BAD_MESSAGE_3);
    }
}
