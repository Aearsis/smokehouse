package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.event.Event;
import cz.eideo.smokehouse.common.event.EventFactory;

import java.io.*;
import java.net.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Common base for multicast endpoints. Implements packet queueing to reduce network traffic.
 */
public class MulticastEndpoint extends Container {

    private final MulticastSocket sock;
    private final MulticastEndpointOptions options;
    private final static Logger logger = Logger.getLogger(MulticastEndpoint.class.getName());

    private final Event flushQueueEvent;
    private final DatagramPacket recvPacket;

    public MulticastEndpoint(EventFactory eventFactory, MulticastEndpointOptions options) throws IOException {
        flushQueueEvent = eventFactory.createEvent(this::flushQueue);
        this.options = options;

        sock = new MulticastSocket(options.port);
        if (options.networkInterface != null) {
            sock.setInterface(options.networkInterface);
        }
        sock.setLoopbackMode(false);
        sock.setTimeToLive(255);
        sock.joinGroup(options.groupAddress);

        final byte[] buf = new byte[options.maxPacketSize];
        recvPacket = new DatagramPacket(buf, buf.length);

        final Thread receiverThread = new Thread(this::run);
        receiverThread.setName("API receiver thread");
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    private void run() {
        while (!sock.isClosed())
            receivePacket();
    }

    /**
     * Receive the packet (in the receiver thread),
     * schedule the processing to the event dispatcher thread,
     * wait for completion, then return.
     * then return.
     */
    private void receivePacket() {
        try {
            sock.receive(recvPacket);
            processPacket(recvPacket);
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Failed receiving and processing packet: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void processPacket(DatagramPacket pkt) throws IOException {
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
                    if (node.isMaster()) {
                        sendValue(node);
                    }
                    break;
                case Packet.VALUE:
                    if (nodes.size() <= packet.key)
                        throw new PacketInvalidException("Received packet for unknown node.");

                    node.readValueFrom(stream);
                    break;
            }
        }
    }

    private final Queue<Packet> packetQueue = new ArrayDeque<>();

    synchronized void transmit(Packet p) {
        packetQueue.add(p);
        flushQueueEvent.schedule();
    }

    public synchronized void flushQueue() {
        try {
            if (packetQueue.isEmpty())
                return;
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
            logger.log(Level.WARNING, "Failed to flush the message queue: " + e.getMessage());
        } finally {
            if (!packetQueue.isEmpty())
                flushQueueEvent.schedule();
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
