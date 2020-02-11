package wumpus;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
@FunctionalInterface
public interface Authenticator {

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

    enum Status {
        UNAUTHENTICATED, AUTHENTICATED;
    }

    Status authenticate(final String token, final String username,
            final String password);

    default Status status(final String token) {
        return authenticate(token, null, null);
    }

}