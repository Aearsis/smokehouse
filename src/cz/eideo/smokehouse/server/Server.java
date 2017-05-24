package cz.eideo.smokehouse.server;

import cz.eideo.smokehouse.common.Session;
import cz.eideo.smokehouse.common.api.MulticastProvider;
import cz.eideo.smokehouse.common.feeder.LogFeeder;
import cz.eideo.smokehouse.common.sensor.SQLiteStoredSensorFactory;
import cz.eideo.smokehouse.common.setup.CubeSetup;
import cz.eideo.smokehouse.common.storage.SQLiteSessionStorage;
import cz.eideo.smokehouse.common.storage.SQLiteStorage;
import cz.eideo.smokehouse.common.util.Posix;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server instance. It initializes the environment for running server.
 * <p>
 * Session name is supplied on start. Server has three responsibilities:
 * 1) Intialize feeders according to the HW
 * 2) Load the session, give it storage and connect feeders
 * 3) Provide network API to the session
 */
class Server {

    private final Session session;
    private final SQLiteStorage db;
    private final ServerOptions options;
    private MulticastProvider serverAPI;

    Server(ServerOptions options) {
        try {
            this.options = options;
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            serverAPI = new MulticastProvider(executorService, options.groupAddress, options.port);

            Thread apiThread = new Thread(serverAPI);
            apiThread.setName("Server API");
            apiThread.setDaemon(true);
            apiThread.start();

            db = SQLiteStorage.fromFile(options.dbName);
            SQLiteSessionStorage storage = new SQLiteSessionStorage(serverAPI, db.getNamespace("session"));

            if (storage.getState() == Session.State.NEW) {
                storage.setSetupClass(CubeSetup.class);
                storage.setState(Session.State.INITIALIZED);
                storage.flush();
            }

            session = new Session(storage);
            session.setAPI(serverAPI);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Nenalezena odpovídající konfigurace udírny.", e);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Nezdařila se inicializace serveru: " + e.getMessage(), e);
        }
    }

    void run() throws Exception {
        session.loadSetup();
        CubeSetup s = (CubeSetup) session.getSetup();

        if (s == null)
            throw new RuntimeException("This server can only work with CubeSetup.");

        s.setSensorFactory(new SQLiteStoredSensorFactory(session.getAPI(), db));
        s.setupSensors();

        LogFeeder logFeeder = new LogFeeder(this.options.logStream, s);
        logFeeder.run();
    }
}

