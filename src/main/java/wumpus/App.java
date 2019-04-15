package wumpus;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.MemoryEntityStore;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.service.CooldownService;
import wumpus.engine.service.ExaminingService;
import wumpus.engine.service.HazardService;
import wumpus.engine.service.LairService;
import wumpus.engine.service.PlayerService;
import wumpus.engine.service.Service;
import wumpus.engine.service.TransitService;
import wumpus.engine.service.WorldService;
import wumpus.io.SessionManager;

/**
 * Application instance for the wumpus game.
 */
public class App implements Runnable {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(App.class.getName());

    /**
     * Default size of a lair.
     */
    private static final int DEFAULT_SIZE = 20;

    /**
     * Duration of a tick in milliseconds.
     */
    private static final int TICK_IN_MILLIS = 10;

    /**
     * Runs a new wumpus App instance using standard output.
     *
     * @param args
     *                 Command line arguments. None specified at this time.
     */
    public static void main(final String... args) {
        new App().run();
    }

    @Override
    public final void run() {
        LOG.info("Welcome to Hunt the Wumpus by Zack Hoffmann!");
        LOG.info("The game engine is starting...");
        LOG.fine("Fine logging is enabled.");

        final EntityStore store = new MemoryEntityStore();

        final Set<Service> services = new HashSet<>();
        services.add(new CooldownService(store));
        services.add(new PlayerService(store));
        services.add(new WorldService(store));
        services.add(new LairService(store, DEFAULT_SIZE));
        services.add(new TransitService(store));
        services.add(new ExaminingService(store));
        services.add(new HazardService(store));

        final ScheduledExecutorService tickService = Executors
                .newScheduledThreadPool(1);
        tickService.scheduleAtFixedRate(
                () -> services.stream()
                        .sorted(Comparator.comparing(Service::priority))
                        .forEach(Service::tick),
                0, TICK_IN_MILLIS, TimeUnit.MILLISECONDS);

        final SessionManager sessions = new SessionManager(store);
        sessions.start();
        LOG.info("The game engine has started.");

        sessions.create();

        long count;
        do {
            count = store.stream().component(Listener.class).count();
        } while (count > 0);

        LOG.info("Shutting down game services.");
        sessions.stop();
        tickService.shutdown();
        try {
            tickService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING,
                    "Interrupted while shutting down game services.", e);
        }

        LOG.info("The game is now shut down.");
    }

}
