package cz.eideo.smokehouse.server;

import cz.cuni.mff.yaclpplib.IllegalOptionValue;
import cz.cuni.mff.yaclpplib.Options;
import cz.cuni.mff.yaclpplib.annotation.Help;
import cz.cuni.mff.yaclpplib.annotation.Mandatory;
import cz.cuni.mff.yaclpplib.annotation.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Help("Options for server")
class ServerOptions implements Options {

    @Option("--db")
    @Help("A SQLite database to store the values.")
    String dbName = "smokehouse.db";

    InetAddress groupAddress = InetAddress.getByAddress(new byte[]{(byte) 239, 42, 84, 18});
    int port = 42424;

    ServerOptions() throws UnknownHostException { }

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

    FileInputStream logStream;

    @Mandatory
    @Option("--logfile")
    @Help("The logfile to read and simulate.")
    void file(String file) {
        File f = new File(file);
        try {
            logStream = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw new IllegalOptionValue("Cannot use logfile " + file + ": " + e.getMessage());
        }
    }


}
