package cz.eideo.smokehouse.common.api;

import cz.cuni.mff.yaclpplib.IllegalOptionValue;
import cz.cuni.mff.yaclpplib.Options;
import cz.cuni.mff.yaclpplib.annotation.*;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Containing class for command line arguments of the API.
 */
@Help("Options for the API")
public class MulticastEndpointOptions implements Options {

    InetAddress groupAddress = InetAddress.getByAddress(new byte[]{(byte) 239, 42, 84, 18});
    int port = 42424;

    @Option("--max-packet-size")
    @Help("Maximum packet size (B, default 1500)")
    public int maxPacketSize = 1500;

    public MulticastEndpointOptions() throws UnknownHostException { }

    @Option(value = "--listen", help = "--listen=[host][:port]")
    @Option("-l")
    @Help("The multicast group (and port) to listen on (default 239.42.84.18:42424)")
    void listen(String on) {
        String[] parts = on.split(":");
        if (!parts[0].isEmpty()) {
            try {
                groupAddress = InetAddress.getByName(parts[0]);
            } catch (UnknownHostException e) {
                throw new RuntimeException("Invalid format of group address.");
            }
        }
        if (parts.length > 1 && !parts[1].isEmpty()) {
            port = Integer.parseInt(parts[1]);
        }
    }

    InetAddress networkInterface = null;

    @Option("--iface")
    @Option("-i")
    @Help("The network interface to listen on (default is the oif for group address)")
    private void findNetworkInterface(String name) {
        try {
            networkInterface = NetworkInterface.getByName(name).getInterfaceAddresses().stream()
                    .findAny()
                    .orElseThrow(() -> new IllegalOptionValue("This interface does not have any address."))
                    .getAddress();
        } catch (SocketException e) {
            throw new IllegalOptionValue(e.getMessage());
        }
    }


}
