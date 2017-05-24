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
abstract class MulticastEndpoint extends Container implements Runnable {

    private final static long aggregateDelay = 200;
    private final static int maxPacketSize = 1500;

    private final ScheduledExecutorService executorService;

    private final MulticastSocket sock;
    private final InetAddress group;
    private final int port;

    MulticastEndpoint(ScheduledExecutorService executorService, InetAddress group, int port) throws IOException {
        this.executorService = executorService;
        this.group = group;
        this.port = port;

        sock = new MulticastSocket(port);
        //sock.setNetworkInterface(NetworkInterface.getByName("enp0s25"));
        sock.setLoopbackMode(false);
        sock.setTimeToLive(255);
        sock.joinGroup(group);
        sock.setSoTimeout(1000);
    }

    @Override
    public void run() {
        while (!sock.isClosed())
            receivePacket();
    }

    private void receivePacket() {
        try {
            byte[] buf = new byte[maxPacketSize];
            DatagramPacket pkt = new DatagramPacket(buf, buf.length);
            sock.receive(pkt);

            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()));

            while (stream.available() > 0) {
                final Packet packet = new Packet(stream);
                processPacket(packet, stream);
            }
        } catch (SocketTimeoutException | PacketInvalidException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void processPacket(Packet packet, DataInputStream stream) throws IOException;

    private final Queue<Packet> packetQueue = new ArrayDeque<>();
    private boolean scheduled = false;

    synchronized void transmit(Packet p) {
        packetQueue.add(p);
        scheduleFlush();
    }

    private void scheduleFlush() {
        if (!scheduled) {
            executorService.schedule(this::flushQueue, aggregateDelay, TimeUnit.MILLISECONDS);
            scheduled = true;
        }
    }

    private synchronized void flushQueue() {
        scheduled = false;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream(maxPacketSize);
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
                    if (os.size() > maxPacketSize)
                        break;

                    len = os.size();
                    packetQueue.remove();
                }
            } catch (IOException ignored) {
            }

            DatagramPacket pkt = new DatagramPacket(buf.toByteArray(), len, group, port);
            sock.send(pkt);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!packetQueue.isEmpty())
                scheduleFlush();
        }
    }

    static class PacketInvalidException extends IOException {
        PacketInvalidException(String s) {
            super(s);
        }
    }

}
