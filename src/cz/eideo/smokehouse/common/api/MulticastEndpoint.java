package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.event.Event;
import cz.eideo.smokehouse.common.event.EventFactory;

import java.io.*;
import java.net.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Common base for multicast endpoints. Implements packet queueing to reduce network traffic.
 */
public class MulticastEndpoint extends Container {

    private final static Logger logger = Logger.getLogger(MulticastEndpoint.class.getName());

    private final MulticastSocket sock;
    private final MulticastEndpointOptions options;
    private final Event flushQueueEvent;
    private final DatagramPacket recvPacket;
    private final Queue<Message> messageQueue = new ArrayDeque<>();

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

    /**
     * Loop until the end of the connection,
     * receiving and processing packets.
     */
    private void run() {
        while (!sock.isClosed())
            receiveAndProcess();
    }

    /**
     * Receive the packet (in the receiver thread) and process it.
     * then return.
     */
    private void receiveAndProcess() {
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
            final Message message = new Message(stream);
            if (message.key >= nodes.size())
                /* The node tree is built while the API is running - we need to know how to build it.
                 * To make it work, just skip nodes we don't have yet.
                 */
                continue;
            Node<?> node = nodes.get(message.key);
            switch (message.type) {
                case Message.QUERY:
                    if (node.isMaster())
                        sendValue(node);
                    break;
                case Message.VALUE:
                    node.readValueFrom(stream);
                    break;
            }
        }
    }


    private synchronized void transmit(Message p) {
        messageQueue.add(p);
        flushQueueEvent.schedule();
    }

    @Override
    public synchronized void flushQueue() {
        try {
            if (messageQueue.isEmpty())
                return;
            ByteArrayOutputStream buf = new ByteArrayOutputStream(options.maxPacketSize);
            DataOutputStream os = new DataOutputStream(buf);
            int len = 0;

            List<Message> sorted = messageQueue.parallelStream()
                    .sorted().distinct().collect(Collectors.toList());
            messageQueue.clear();
            messageQueue.addAll(sorted);

            try {
                while (!messageQueue.isEmpty()) {
                    Message head = messageQueue.element();
                    head.write(os);
                    if (os.size() > options.maxPacketSize)
                        break;

                    len = os.size();
                    messageQueue.remove();
                }
            } catch (IOException ignored) {
            }

            DatagramPacket pkt = new DatagramPacket(buf.toByteArray(), len, options.groupAddress, options.port);
            sock.send(pkt);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to flush the message queue: " + e.getMessage());
        } finally {
            if (!messageQueue.isEmpty())
                flushQueueEvent.schedule();
        }
    }

    @Override
    public void sendQuery(Node node) {
        assert node.getEndpoint() == this;
        transmit(new Message(Message.QUERY, node.getApiKey()));
    }

    @Override
    public void sendValue(Node node) throws IOException {
        assert node.getEndpoint() == this;
        transmit(Message.valueFromNode(node.getApiKey(), node));
    }

}
