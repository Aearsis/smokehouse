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
 * Simple debugging client instance. This client just dumps any source that changes its value.
 * It's not very convenient for real smoking, but serves well to test the functionality.
 * <p>
 * Client communicates with server using multicast API to avoid having to specify server address.
 * The typical case is when server and client are both on the same L2 network, on which multicast
 * typically works well.
 */
class Client {

    private final Session session;

    private final NodeFactory nodeFactory;
    private final Dispatcher eventDispatcher = new Dispatcher();

    private final ClientOptions options;

    Client(ClientOptions options) {
        try {
            this.options = options;
            MulticastEndpoint clientAPI = new MulticastEndpoint(eventDispatcher, options.multicastEndpointOptions);

            nodeFactory = RemoteNode.createNodeFactory(clientAPI);
            SessionStorage storage = new SessionStorage(nodeFactory);

            session = new Session(storage, clientAPI);
        } catch (IOException e) {
            throw new RuntimeException("Nezdařila se inicializace klienta: " + e.getMessage());
        }
    }

    void run() {
        session.loadSetup();
        CubeSetup s = (CubeSetup) session.getSetup();
        if (s == null)
            throw new RuntimeException("Pro tento setup není klient připraven.");

        s.setSensorFactory(new DefaultSensorFactory(nodeFactory, eventDispatcher));
        s.setupSensors();

        CubeConsoleDumper dumper = new CubeConsoleDumper(s);

        s.getCubeArea().attachObserver(eventDispatcher.createEvent(dumper::dump));

        eventDispatcher.run();
    }
}

