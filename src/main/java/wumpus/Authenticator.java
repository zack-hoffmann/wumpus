package wumpus;

/**
 * TODO
 */
@FunctionalInterface
public interface Authenticator {

    enum Status {
        UNKNOWN, USERNAME_NEEDED, PASSWORD_NEEDED, AUTHENTICATED, EXPIRED, LOCKED;
    }

    Status authenticate(final String token, final String username,
            final String password);

    default Status status(final String token) {
        return authenticate(token, null, null);
    }

    default Status handleResponse(final String token, final String prompt,
            final String value) {
        switch (prompt) {
        case "USERNAME":
            return authenticate(token, value, null);
        case "PASSWORD":
            return authenticate(token, null, value);
        default:
            return status(token);
        }
    }
}