package cz.eideo.smokehouse.server;

import cz.cuni.mff.yaclpplib.ArgumentParser;
import cz.cuni.mff.yaclpplib.ArgumentParserFactory;
import cz.eideo.smokehouse.common.api.MulticastEndpointOptions;

/**
 * Entry point for the server application. Only parses parameters and runs a server instance.
 */
class Bootstrap {

    public static void main(String[] args) {
        try {
            ArgumentParser parser = ArgumentParserFactory.create();
            ServerOptions serverOptions = parser.addOptions(new ServerOptions());
            parser.addOptions(serverOptions.multicastEndpointOptions);
            parser.parse(args);

            Server server = new Server(serverOptions);
            server.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
