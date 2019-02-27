package wumpus;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import wumpus.engine.command.CommandLibrary;
import wumpus.engine.command.Debug;
import wumpus.engine.command.East;
import wumpus.engine.command.Move;
import wumpus.engine.command.North;
import wumpus.engine.command.Quit;
import wumpus.engine.command.Shoot;
import wumpus.engine.command.South;
import wumpus.engine.command.West;
import wumpus.engine.entity.EntityStore;
import wumpus.engine.entity.MemoryEntityStore;
import wumpus.engine.entity.component.Listener;
import wumpus.engine.service.HazardService;
import wumpus.engine.service.LairService;
import wumpus.engine.service.PlayerService;
import wumpus.engine.service.Service;
import wumpus.engine.service.TransitService;
import wumpus.engine.service.WumpusService;
import wumpus.io.StandardIOAdapter;

/**
 * Application instance for the wumpus game.
 */
public class App implements Runnable {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(App.class.getName());

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

        final ExecutorService ioServ = Executors.newCachedThreadPool();
        final StandardIOAdapter io = new StandardIOAdapter(ioServ);
        final EntityStore store = new MemoryEntityStore();
        final CommandLibrary lib = new CommandLibrary(new Quit(), new Move(),
                new North(), new East(), new South(), new West(), new Shoot(),
                new Debug());

        final PlayerService players = new PlayerService(store);
        final long player = players.createPlayer();
        players.attachPlayer(player, io);
        final Set<Service> services = new HashSet<>();
        services.add(players);
        services.add(new LairService(store));
        services.add(new TransitService(store));
        services.add(new WumpusService(store));
        services.add(new HazardService(store));

        final ScheduledExecutorService tickService = Executors
                .newScheduledThreadPool(1);
        tickService.scheduleAtFixedRate(
                () -> services.stream().forEach(Service::tick), 0,
                TICK_IN_MILLIS, TimeUnit.MILLISECONDS);

        LOG.info("The game engine has started.");

        io.post("\nWelcome to Hunt the Wumpus by Zack Hoffmann!");

        while (store.get(player).get().hasComponent(Listener.class)) {
            Optional<String> i = io.poll();
            if (i.isPresent()) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Input found: " + i.get());
                }
                final String[] tokens = i.get().split("\\s+");
                if (tokens.length > 0 && tokens[0].trim().length() > 0) {
                    final String response = lib.execute(tokens[0], player,
                            store,
                            Arrays.copyOfRange(tokens, 1, tokens.length));
                    io.post(response);
                }
            }
        }
        io.post("\nThank you for playing!\n<Press Enter to close>\n");

        LOG.info("Shutting down I/O.");
        try {
            io.shutdown();
        } catch (IOException e) {
            LOG.log(Level.WARNING,
                    "Could not shut down I/O adapter completely.", e);
        }
        ioServ.shutdown();
        try {
            ioServ.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Interrupted while shutting down I/O.", e);
        }

        LOG.info("Shutting down game services.");
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
