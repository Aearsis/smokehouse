package cz.eideo.smokehouse.common.api;

import java.io.*;
import java.net.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Common base for multicast endpoints. Implements packet queueing to reduce network traffic.
 */
public class MulticastEndpoint extends Container implements Runnable {

    private final ScheduledExecutorService executorService;

    private final MulticastSocket sock;
    private final MulticastEndpointOptions options;

    public MulticastEndpoint(ScheduledExecutorService executorService, MulticastEndpointOptions options) throws IOException {
        this.executorService = executorService;
        this.options = options;

        sock = new MulticastSocket(options.port);
        //sock.setNetworkInterface(NetworkInterface.getByName("enp0s25"));
        sock.setLoopbackMode(false);
        sock.setTimeToLive(255);
        sock.joinGroup(options.groupAddress);
        sock.setSoTimeout(1000);
    }

    @Override
    public void run() {
        while (!sock.isClosed())
            receivePacket();
    }

    private void receivePacket() {
        try {
            byte[] buf = new byte[options.maxPacketSize];
            DatagramPacket pkt = new DatagramPacket(buf, buf.length);
            sock.receive(pkt);

            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()));

            while (stream.available() > 0) {
                final Packet packet = new Packet(stream);
                if (packet.key >= nodes.size())
                    /* The node tree is built while the API is running - we need to know how to build it.
                     * To make it work, just skip nodes we don't have yet.
                     */
                    continue;
                Node<?> node = nodes.get(packet.key);
                switch (packet.type) {
                    case Packet.QUERY:
                        if (node.isMaster())
                            sendValue(node);
                        break;
                    case Packet.VALUE:
                        if (nodes.size() <= packet.key)
                            throw new PacketInvalidException("Received packet for unknown node.");

                        node.readValueFrom(stream);
                        break;
                }
            }
        } catch (SocketTimeoutException | PacketInvalidException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Queue<Packet> packetQueue = new ArrayDeque<>();
    private boolean scheduled = false;

    synchronized void transmit(Packet p) {
        packetQueue.add(p);
        scheduleFlush();
    }

    private void scheduleFlush() {
        if (!scheduled) {
            executorService.schedule(this::flushQueue, options.aggregateDelay, TimeUnit.MILLISECONDS);
            scheduled = true;
        }
    }

    private synchronized void flushQueue() {
        scheduled = false;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream(options.maxPacketSize);
            DataOutputStream os = new DataOutputStream(buf);
            int len = 0;

            List<Packet> sorted = packetQueue.parallelStream()
                    .sorted().distinct().collect(Collectors.toList());
            packetQueue.clear();
            packetQueue.addAll(sorted);

            try {
                while (!packetQueue.isEmpty()) {
                    Packet head = packetQueue.element();
                    head.write(os);
                    if (os.size() > options.maxPacketSize)
                        break;

                    len = os.size();
                    packetQueue.remove();
                }
            } catch (IOException ignored) {
            }

            DatagramPacket pkt = new DatagramPacket(buf.toByteArray(), len, options.groupAddress, options.port);
            sock.send(pkt);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!packetQueue.isEmpty())
                scheduleFlush();
        }
    }

    @Override
    public void sendQuery(Node node) {
        assert node.getEndpoint() == this;
        transmit(new Packet(Packet.QUERY, node.getApiKey()));
    }

    @Override
    public void sendValue(Node node) throws IOException {
        assert node.getEndpoint() == this;
        transmit(Packet.valueFromNode(node.getApiKey(), node));
    }

    static class PacketInvalidException extends IOException {
        PacketInvalidException(String s) {
            super(s);
        }
    }

}
