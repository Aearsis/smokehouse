package cz.eideo.smokehouse.client;

import cz.eideo.smokehouse.common.Session;
import cz.eideo.smokehouse.common.SessionStorage;
import cz.eideo.smokehouse.common.api.MulticastEndpoint;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.RemoteNode;
import cz.eideo.smokehouse.common.event.Dispatcher;
import cz.eideo.smokehouse.common.sensor.DefaultSensorFactory;
import cz.eideo.smokehouse.common.setup.CubeSetup;

import java.io.IOException;

/**
 * Simple command line client.
 * <p>
 * Client communicates with server using multicast API to avoid having to specify server address.
 * The typical case is when server and client are both on the same L2 network, on which multicast
 * typically works well.
 */
class Client {

    private final Dispatcher eventDispatcher = new Dispatcher();

    Client(ClientOptions options) throws IOException {
        // Initialize the API
        final MulticastEndpoint clientAPI = new MulticastEndpoint(eventDispatcher, options.multicastEndpointOptions);

        // Nodes will be RemoteNodes
        final NodeFactory nodeFactory = RemoteNode.createNodeFactory(clientAPI);

        // Use the simplest session storage
        final SessionStorage storage = new SessionStorage(nodeFactory);

        // Make sure the setup is the CubeSetup
        if (!storage.getSetupClass().equals(CubeSetup.class))
            throw new RuntimeException("This server can only work with CubeSetup.");

        // Load the setup
        final Session session = new Session(storage, clientAPI);
        session.loadSetup();

        CubeSetup s = (CubeSetup) session.getSetup();
        assert s != null;

        // Create sensors as RemoteNodes
        s.setSensorFactory(new DefaultSensorFactory(nodeFactory, eventDispatcher));
        s.setupSensors();

        // Create the dumper and attach the event
        final CubeConsoleDumper dumper = new CubeConsoleDumper(s);
        s.getCubeArea().attachObserver(eventDispatcher.createEvent(dumper::dump));
    }

    void run() {
        // Let the dispatcher do the hard job
        eventDispatcher.run();
    }
}

