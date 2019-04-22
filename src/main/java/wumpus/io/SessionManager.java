package wumpus.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import wumpus.engine.command.CommandLibrary;
import wumpus.engine.entity.Entity;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.component.Listener;

/**
 * Manages IO sessions.
 */
public final class SessionManager {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger
            .getLogger(SessionManager.class.getName());

    /**
     * Game sessions.
     */
    private final Set<Session> sessions;

    /**
     * Executor for IO.
     */
    private final ExecutorService service;

    /**
     * Game entity store.
     */
    private final EntityStore store;

    /**
     * Game commands.
     */
    private final CommandLibrary library;

    /**
     * Running status of session manager.
     */
    private boolean running;

    /**
     * Create a session manager.
     *
     * @param s
     *              the entity store this manager should use
     */
    public SessionManager(final EntityStore s) {
        sessions = ConcurrentHashMap.newKeySet();
        service = Executors.newCachedThreadPool();
        library = new CommandLibrary();
        store = s;
        running = true;
    }

    /**
     * The main operation of the session manager.
     */
    private void run() {
        while (running) {
            sessions.stream().forEach(s -> {
                final Optional<Entity> p = store.get(s.getEntityId());
                if (s.isOpen() && p.isPresent()
                        && p.get().hasComponent(Listener.class)) {
                    Optional<String> i = s.getIO().poll();
                    if (i.isPresent()) {
                        final String[] tokens = i.get().split("\\s+");
                        if (tokens.length > 0
                                && tokens[0].trim().length() > 0) {
                            final String response = library.execute(tokens[0],
                                    s.getEntityId(), store, Arrays.copyOfRange(
                                            tokens, 1, tokens.length));
                            s.getIO().post(response);
                        }
                    }
                } else {
                    try {
                        s.getIO().shutdown();
                    } catch (IOException ex) {
                        LOG.log(Level.WARNING,
                                "Could not shut down I/O adapter completely.",
                                ex);
                    }
                    p.ifPresent(e -> {
                        e.deregisterComponent(Listener.class);
                        store.commit(e);
                    });
                }
            });
        }
    }

    /**
     * Begin operation of the session manager.
     */
    public void start() {
        LOG.info("Starting IO.");
        service.execute(this::run);
    }

    /**
     * Stop the session manager. It cannot be restarted.
     */
    public void stop() {
        running = false;
        LOG.info("Shutting down I/O.");
        service.shutdown();
    }

    /**
     * Add a new player session to the manager.
     *
     * At this time only standard IO sessions are supported.
     */
    public void create() {
        final StandardIOAdapter io = new StandardIOAdapter(service);
        io.post("\nWelcome to Hunt the Wumpus by Zack Hoffmann!");
        final Entity player = store.create();
        player.registerComponent(new Listener(m -> {
            io.post("\n" + m.toString());
        }));
        store.commit(player);
        sessions.add(new Session(player.getId(), io));
    }
}
