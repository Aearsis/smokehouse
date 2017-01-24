package cz.eideo.smokehouse.common.api;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The server side endpoint listening to queries
 * and announcing changes and updates in multicast group.
 */
public class MulticastProvider extends MulticastEndpoint {

    public MulticastProvider(ScheduledExecutorService executorService, InetAddress group, int port) throws IOException {
        super(executorService, group, port);
    }

    @Override
    public boolean announceValueChanged(int key) {
        try {
            transmit(Packet.valueFromNode(key, nodes.get(key)));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void processPacket(Packet packet) {
        switch (packet.type) {
            case Packet.QUERY:
                announceValueChanged(packet.key);
                break;
        }
    }

}
