package cz.eideo.smokehouse.server;

import cz.eideo.smokehouse.common.Session;
import cz.eideo.smokehouse.common.api.MulticastProvider;
import cz.eideo.smokehouse.common.sensor.SQLiteStoredSensorFactory;
import cz.eideo.smokehouse.common.setup.CubeSetup;
import cz.eideo.smokehouse.common.storage.SQLiteSessionStorage;
import cz.eideo.smokehouse.common.storage.SQLiteStorage;
import cz.eideo.smokehouse.showcase.CubeRandomFeeder;
import sun.misc.Signal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Server instance. It initializes the environment for running server.
 *
 * Session name is supplied on start. Server has three responsibilities:
 *      1) Intialize feeders according to the HW
 *      2) Load the session, give it storage and connect feeders
 *      3) Provide network API to the session
 */
class Server {

    private final Session session;
    private final SQLiteStorage db;

    ServerArguments args;
    MulticastProvider serverAPI;

    Server (ServerArguments args) {
        this.args = args;
        try {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            serverAPI = new MulticastProvider(executorService, args.groupAddress, args.port);

            Thread apiThread = new Thread(serverAPI);
            apiThread.setName("Server API");
            apiThread.setDaemon(true);
            apiThread.start();

            db = SQLiteStorage.fromFile(args.dbName);
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

    void run() {
        session.loadSetup();
        CubeSetup s = (CubeSetup) session.getSetup();

        if (s == null)
            throw new RuntimeException("This server can only work with CubeSetup.");

        s.setSensorFactory(new SQLiteStoredSensorFactory(session.getAPI(), db));
        s.setupSensors();

        CubeRandomFeeder feeder = new CubeRandomFeeder(s);

        feeder.start();

        Signal.handle(new Signal("TERM"), signal -> feeder.stop());

    }
}

