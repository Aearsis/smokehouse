package cz.eideo.smokehouse.server;

import cz.cuni.mff.yaclpplib.ArgumentParser;
import cz.cuni.mff.yaclpplib.ArgumentParserFactory;

/**
 * Entry point for the server application. Only parses parameters and runs a server instance.
 */
class Bootstrap {

    public static void main(String[] argv) {
        try {
            ArgumentParser parser = ArgumentParserFactory.create();
            ServerOptions args = parser.addOptions(new ServerOptions());
            parser.parse(argv);

            Server server = new Server(args);
            server.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
