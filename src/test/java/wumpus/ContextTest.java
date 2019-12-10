package wumpus;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test suite for the application context.
 */
public final class ContextTest {

    /**
     * File name of a non-existent properties file.
     */
    private static final String BAD_PROP_FILE_NAME = "bad.properties";

    /**
     * File name to use for testing.
     */
    private static final String TEST_PROP_FILE_NAME = "test.properties";

    /**
     * Key not defined in any properties.
     */
    private static final String BAD_KEY = "food";

    /**
     * Key defined only in the default properties.
     */
    private static final String DEFAULT_ONLY_KEY = "foo";

    /**
     * Value found only in the default properties.
     */
    private static final String DEFAULT_ONLY_VAL = "bar";

    /**
     * Key defined only in the rundir properties.
     */
    private static final String RUNDIR_ONLY_KEY = "rundir";

    /**
     * Value found only in the rundir properties.
     */
    private static final String RUNDIR_ONLY_VAL = "only";

    /**
     * Key defined in both properties.
     */
    private static final String SHARED_KEY = "foo2";

    /**
     * Value for the key defined in both properties and found in the rundir
     * properties.
     */
    private static final String SHARED_RUNDIR_VAL = "bar3";

    /**
     * Contents of the rundir property file to generate for testing.
     */
    private static final List<String> RUNDIR_PROP_LINES = Arrays.asList(
            SHARED_KEY + "=" + SHARED_RUNDIR_VAL,
            RUNDIR_ONLY_KEY + "=" + RUNDIR_ONLY_VAL);

    /**
     * Set up the rundir properties file for testing.
     *
     * @throws IOException
     *                         if the file cannot be created
     */
    @BeforeClass
    public static void setupRundirProps() throws IOException {
        Files.write(
                Paths.get(System.getProperty("user.dir"))
                        .resolve(TEST_PROP_FILE_NAME),
                RUNDIR_PROP_LINES, Charset.forName("UTF-8"),
                StandardOpenOption.CREATE_NEW);
    }

    /**
     * Delete the rundir properties file used by testing.
     *
     * @throws IOException
     *                         if the file cannot be deleted
     */
    @AfterClass
    public static void deleteRundirProps() throws IOException {
        Files.delete(Paths.get(System.getProperty("user.dir"))
                .resolve(TEST_PROP_FILE_NAME));
    }

    /**
     * Not having a default properties file should cause a crash.
     */
    @Test(expected = RuntimeException.class)
    public void noDefaultProperties() {
        Context.create(BAD_PROP_FILE_NAME).property(DEFAULT_ONLY_KEY);
    }

    /**
     * Selecting an undefined property should return an empty reference.
     */
    @Test
    public void noProperty() {
        Assert.assertTrue(Context.create(TEST_PROP_FILE_NAME).property(BAD_KEY)
                .isEmpty());
    }

    /**
     * Selecting a property defined in only the default properties file should
     * return its value in that file.
     */
    @Test
    public void foundDefaultProperty() {
        Assert.assertEquals(DEFAULT_ONLY_VAL, Context
                .create(TEST_PROP_FILE_NAME).property(DEFAULT_ONLY_KEY).get());
    }

    /**
     * Selecting a property defined only in the rundir properties file should
     * return its value in that file.
     */
    @Test
    public void foundRundirProperty() {
        Assert.assertEquals(RUNDIR_ONLY_VAL, Context.create(TEST_PROP_FILE_NAME)
                .property(RUNDIR_ONLY_KEY).get());
    }

    /**
     * Selecting a property defined in both the default and rundir properties
     * file should return its value in the rundir properties file.
     */
    @Test
    public void foundOverrideProperty() {
        Assert.assertEquals(SHARED_RUNDIR_VAL,
                Context.create(TEST_PROP_FILE_NAME).property(SHARED_KEY).get());
    }
}
