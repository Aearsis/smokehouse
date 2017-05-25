package cz.eideo.smokehouse.server;

import cz.eideo.smokehouse.common.Session;
import cz.eideo.smokehouse.common.api.LocalNode;
import cz.eideo.smokehouse.common.api.MulticastEndpoint;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.event.Dispatcher;
import cz.eideo.smokehouse.common.feeder.LogFeeder;
import cz.eideo.smokehouse.common.sensor.SQLiteStoredSensorFactory;
import cz.eideo.smokehouse.common.setup.CubeSetup;
import cz.eideo.smokehouse.common.storage.SQLiteSessionStorage;
import cz.eideo.smokehouse.common.storage.SQLiteStorage;

import java.io.IOException;
import java.sql.SQLException;

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
    private final NodeFactory nodeFactory;

    private final Dispatcher eventDispatcher = new Dispatcher();

    Server(ServerOptions options) {
        try {
            this.options = options;
            MulticastEndpoint serverAPI = new MulticastEndpoint(eventDispatcher, options.multicastEndpointOptions);

            db = SQLiteStorage.fromFile(options.dbName);
            nodeFactory = LocalNode.createNodeFactory(serverAPI);
            SQLiteSessionStorage storage = new SQLiteSessionStorage(nodeFactory, db.getNamespace("session"));

            if (storage.getState() == Session.State.NEW) {
                storage.setSetupClass(CubeSetup.class);
                storage.setState(Session.State.INITIALIZED);
                storage.flush();
            }

            session = new Session(storage, serverAPI);
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

        s.setSensorFactory(new SQLiteStoredSensorFactory(nodeFactory, eventDispatcher, db));
        s.setupSensors();

        LogFeeder logFeeder = new LogFeeder(options.logStream, s, options.replaySpeedup, eventDispatcher);
        logFeeder.run();

        eventDispatcher.run();
    }
}

