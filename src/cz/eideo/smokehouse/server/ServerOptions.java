package cz.eideo.smokehouse.server;

import cz.cuni.mff.yaclpplib.IllegalOptionValue;
import cz.cuni.mff.yaclpplib.Options;
import cz.cuni.mff.yaclpplib.annotation.Help;
import cz.cuni.mff.yaclpplib.annotation.Mandatory;
import cz.cuni.mff.yaclpplib.annotation.Option;
import cz.eideo.smokehouse.common.api.MulticastEndpointOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Help("Options for the server")
class ServerOptions implements Options {

    public MulticastEndpointOptions multicastEndpointOptions = new MulticastEndpointOptions();

    ServerOptions() throws UnknownHostException { }

    @Option("--db")
    @Help("A SQLite database to store the values.")
    String dbName = "smokehouse.db";

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
