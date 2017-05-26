package cz.eideo.smokehouse.server;

import cz.cuni.mff.yaclpplib.ArgumentParser;
import cz.cuni.mff.yaclpplib.ArgumentParserFactory;
import cz.cuni.mff.yaclpplib.MissingMandatoryOptionException;

/**
 * Entry point for the server application. Only parses parameters and runs a server instance.
 */
class Bootstrap {

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParserFactory.create();
        try {
            ServerOptions serverOptions = parser.addOptions(new ServerOptions());
            parser.parse(args);

            Server server = new Server(serverOptions);
            server.run();
        } catch (MissingMandatoryOptionException e) {
            System.err.println(e.getMessage());
            parser.printHelp();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
