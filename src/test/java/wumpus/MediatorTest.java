package wumpus;

import org.junit.Assert;
import org.junit.Test;

import wumpus.external.Mediator;

/**
 * Mediator error handling test suite.
 *
 * @see wumpus.Mediator
 */
public final class MediatorTest {

    /**
     * Test value.
     */
    private static final String FOO = "foo";

    /**
     * An unobtainable attempted value should return an empty reference.
     */
    @Test
    public void failedSupplyAttempt() {
        Assert.assertTrue(Mediator.attempt(() -> {
            throw new RuntimeException();
        }));
    }

    /**
     * An attempted value which is null should return an empty reference.
     */
    @Test
    public void successfulNullSupplyAttempt() {
        Assert.assertTrue(Mediator.attempt(() -> {
            return null;
        }));
    }

    /**
     * An attempted non-null value should return a reference to that value.
     */
    @Test
    public void successfulSupplyAttempt() {
        Assert.assertEquals(FOO, Mediator.attempt(() -> {
            return FOO;
        }));
    }

    /**
     * TODO Not testing at this time. Need to check log output.
     */
    @Test
    public void failedConsumeAttempt() {
        Mediator.attempt(i -> {
            throw new RuntimeException();
        }, FOO);
    }

    /**
     * TODO Not testing at this time. Need to check absence of log output.
     */
    @Test
    public void successfulConsumeAttempt() {
        Mediator.attempt(i -> {
        }, FOO);
    }

    /**
     * A required value which cannot be obtained should crash.
     */
    @Test(expected = RuntimeException.class)
    public void failedSupplyRequire() {
        Mediator.require(() -> {
            throw new RuntimeException();
        });
    }

    /**
     * A required value which is null should crash.
     */
    @Test(expected = RuntimeException.class)
    public void failedNullSupplyRequire() {
        Mediator.require(() -> {
            return null;
        });
    }

    /**
     * An obtainable value should equal itself.
     */
    @Test
    public void successfulSupplyRequire() {
        Assert.assertEquals(FOO, Mediator.require(() -> {
            return FOO;
        }));
    }

    /**
     * A value which cannot be consumed should crash.
     */
    @Test(expected = RuntimeException.class)
    public void failedConsumeRequire() {
        Mediator.require(i -> {
            throw new RuntimeException();
        }, FOO);
    }

    /**
     * TODO Not testing at this time. Need to check absence of log output.
     */
    @Test
    public void successfulConsumeRequire() {
        Mediator.attempt(i -> {
        }, FOO);
    }
}
