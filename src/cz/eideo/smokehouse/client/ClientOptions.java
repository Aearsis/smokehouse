package cz.eideo.smokehouse.client;

import cz.cuni.mff.yaclpplib.Options;
import cz.cuni.mff.yaclpplib.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Containing class for command line arguments of the client.
 */
@Help("Options for client")
class ClientOptions implements Options {

    InetAddress groupAddress = InetAddress.getByAddress(new byte[]{(byte) 239, 42, 84, 18});
    int port = 42424;

    ClientOptions() throws UnknownHostException { }

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

}
