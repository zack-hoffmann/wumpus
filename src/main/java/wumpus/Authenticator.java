package wumpus;

/**
 * TODO
 */
@FunctionalInterface
public interface Authenticator {

    enum Status {
        UNKNOWN, AUTHENTICATED, EXPIRED, LOCKED;
    }

    Status authenticate(final String token, final String username,
            final String password);

    default Status status(final String token) {
        return authenticate(token, null, null);
    }

}