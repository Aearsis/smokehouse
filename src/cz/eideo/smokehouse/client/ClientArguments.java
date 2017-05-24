package cz.eideo.smokehouse.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Containing class for command line arguments of the client.
 */
class ClientArguments {

    InetAddress groupAddress = InetAddress.getByAddress(new byte[]{(byte) 239, 42, 84, 18});
    int port = 42424;

    private ClientArguments() throws UnknownHostException {
    }

    static ClientArguments parseFromArgv(String[] argv) throws UnknownHostException {
        ClientArguments a = new ClientArguments();

        if (argv.length >= 1)
            a.groupAddress = InetAddress.getByName(argv[1]);

        if (argv.length >= 2)
            a.port = Integer.parseInt(argv[3]);

        return a;
    }
}
