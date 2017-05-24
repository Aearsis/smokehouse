package cz.eideo.smokehouse.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

class ServerArguments {
    String dbName;
    InetAddress groupAddress = InetAddress.getByAddress(new byte[]{(byte) 239, 42, 84, 18});
    int port = 42424;

    private ServerArguments() throws UnknownHostException {
    }

    static ServerArguments parseFromArgv(String[] argv) throws UnknownHostException {
        if (argv.length < 1)
            throw new RuntimeException("arguments: <db file> [multicast group] [port]");

        ServerArguments a = new ServerArguments();
        a.dbName = argv[0];

        if (argv.length >= 2)
            a.groupAddress = InetAddress.getByName(argv[1]);

        if (argv.length >= 3)
            a.port = Integer.parseInt(argv[3]);

        return a;
    }
}
