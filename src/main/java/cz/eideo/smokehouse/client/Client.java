package cz.eideo.smokehouse.client;

import cz.eideo.smokehouse.common.Session;
import cz.eideo.smokehouse.common.SessionStorage;
import cz.eideo.smokehouse.common.api.MulticastConsumer;
import cz.eideo.smokehouse.common.sensor.DefaultSensorFactory;
import cz.eideo.smokehouse.common.setup.CubeSetup;
import sun.misc.Signal;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Simple debugging client instance. This client just dumps any source that changes its value.
 * It's not very convenient for real smoking, but serves well to test the functionality.
 *
 * Client communicates with server using multicast API to avoid having to specify server address.
 * The typical case is when server and client are both on the same L2 network, on which multicast
 * typically works well.
 */
class Client {

    private final Session session;

    MulticastConsumer clientAPI;

    Client(ClientArguments args) {
        try {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            clientAPI = new MulticastConsumer(executorService, args.groupAddress, args.port);
            Thread apiThread = new Thread(clientAPI);
            apiThread.setName("Client API");
            apiThread.setDaemon(true);
            apiThread.start();

            SessionStorage storage = new SessionStorage(clientAPI);

            session = new Session(storage);
            session.setAPI(clientAPI);
        } catch (IOException e) {
            throw new RuntimeException("Nezdařila se inicializace klienta: " + e.getMessage());
        }
    }

    void run() {
        session.loadSetup();
        CubeSetup s = (CubeSetup) session.getSetup();
        if (s == null)
            throw new RuntimeException("Pro tento setup není klient připraven.");

        s.setSensorFactory(new DefaultSensorFactory(clientAPI));
        s.setupSensors();

        CubeConsoleDumper dumper = new CubeConsoleDumper(s);
        dumper.start();

        Signal.handle(new Signal("TERM"), signal -> {
            dumper.stop();
        });
    }
}

