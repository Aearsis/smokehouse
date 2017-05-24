package cz.eideo.smokehouse.common.api;

import java.io.DataInputStream;
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
    protected void processPacket(Packet packet, DataInputStream stream) throws IOException {
        switch (packet.type) {
            case Packet.QUERY:
                announceValueChanged(packet.key);

                break;
            case Packet.VALUE:
                if (nodes.size() <= packet.key)
                    throw new PacketInvalidException("Received packet for unknown node.");

                // We don't need the value, but we need to advance the stream correctly
                nodes.get(packet.key).getCodec().decode(stream);
                break;
        }
    }

}
