package cz.eideo.smokehouse.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientArguments {

    public InetAddress groupAddress = InetAddress.getByAddress(new byte[] {(byte) 239, 42, 84, 18} );
    public int port = 42424;

    public ClientArguments() throws UnknownHostException {
    }

    public static ClientArguments parseFromArgv(String[] argv) throws UnknownHostException {
        ClientArguments a = new ClientArguments();

        if (argv.length >= 1)
            a.groupAddress = InetAddress.getByName(argv[1]);

        if (argv.length >= 2)
            a.port = Integer.parseInt(argv[3]);

        return a;
    }
}
