package cz.eideo.smokehouse.common.api;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The client side endpoint listening to value changes
 * announced in a multicast group.
 */
public class MulticastConsumer extends MulticastEndpoint {

    public MulticastConsumer(ScheduledExecutorService executorService, InetAddress group, int port) throws IOException {
        super(executorService, group, port);
    }

    @Override
    public synchronized boolean announceQueryValue(int key) {
       transmit(new Packet(Packet.QUERY, key));
       return true;
    }

    @Override
    protected void processPacket(Packet packet) throws IOException {
        switch (packet.type) {
            case Packet.VALUE :
                if (nodes.size() <= packet.key)
                    throw new RuntimeException("Received packet for unknown node.");

                packet.valueToNode(nodes.get(packet.key));
                break;
        }
    }

}
