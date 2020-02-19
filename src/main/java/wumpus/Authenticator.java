package wumpus;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to determine whether or not user credentials and/or tokens are
 * authentic.
 */
@FunctionalInterface
public interface Authenticator {

    /**
     * Create authenticator instance.
     *
     * @param app
     *                the application instance
     * @return the new authenticator
     */
    static Authenticator create(final App app) {
        // TODO database config?
        final Map<String, String> loginDummy = new HashMap<>();
        loginDummy.put("ADMIN", "ADMIN");

        final Map<String, Status> tokenDummy = new HashMap<>();

        return (t, u, p) -> {
            if (u != null && p != null) {
                if (loginDummy.get(u) != null && loginDummy.get(u).equals(p)) {
                    tokenDummy.put(t, Status.AUTHENTICATED);
                }
            }

            return tokenDummy.getOrDefault(t, Status.UNAUTHENTICATED);
        };
    }

    /**
     * Authentication state of a token.
     */
    enum Status {
        /**
         * The token is not authenticated.
         */
        UNAUTHENTICATED,
        /**
         * The token is authenticated.
         */
        AUTHENTICATED;
    }

    /**
     * For a given token, username, and password combination assess and/or
     * update the authentication status of the token.
     *
     * @param token
     *                     the token to authenticate
     * @param username
     *                     the username to associate with the token
     * @param password
     *                     the password to associate with the token
     * @return the status of the token
     */
    Status authenticate(String token, String username, String password);

    /**
     * Get the authentication status for a token.
     *
     * @param token
     *                  the token to get the status of
     * @return the authentication status of the token
     */
    default Status status(final String token) {
        return authenticate(token, null, null);
    }

}
