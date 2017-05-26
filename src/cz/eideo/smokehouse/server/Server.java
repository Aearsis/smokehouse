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
 */
class Server {

    private final LogFeeder logFeeder;

    private final Dispatcher eventDispatcher = new Dispatcher();

    Server(ServerOptions options) throws IOException, SQLException, ClassNotFoundException {
        // Create and run the API endpoint
        MulticastEndpoint serverAPI = new MulticastEndpoint(eventDispatcher, options.multicastEndpointOptions);

        // Nodes inside sensors will be LocalNodes
        NodeFactory nodeFactory = LocalNode.createNodeFactory(serverAPI);

        // Initialize storage
        SQLiteStorage db = SQLiteStorage.fromFile(options.dbName);
        SQLiteSessionStorage storage = new SQLiteSessionStorage(nodeFactory, db.getNamespace("session"));

        // New session starting?
        if (storage.getState() == Session.State.NEW) {
            storage.setSetupClass(CubeSetup.class);
            storage.setState(Session.State.INITIALIZED);
            storage.flush();
        }

        // Make sure the setup is the CubeSetup
        if (!storage.getSetupClass().equals(CubeSetup.class))
            throw new RuntimeException("This server can only work with CubeSetup.");

        // Create and run the session
        Session session = new Session(storage, serverAPI);
        session.loadSetup();

        CubeSetup s = (CubeSetup) session.getSetup();
        assert s != null;

        // Create the sensors, backed by SQLite
        s.setSensorFactory(new SQLiteStoredSensorFactory(nodeFactory, eventDispatcher, db));
        s.setupSensors();

        // Initialize the feeder
        logFeeder = new LogFeeder(options.logStream, s.getCubeArea(), options.replaySpeedup, eventDispatcher);
    }

    void run() {
        // Schedule the first feeding event...
        logFeeder.run();

        // ... and finish with the event loop
        eventDispatcher.run();
    }
}

