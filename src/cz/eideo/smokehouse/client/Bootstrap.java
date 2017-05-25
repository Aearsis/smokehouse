package cz.eideo.smokehouse.client;

import cz.cuni.mff.yaclpplib.ArgumentParser;
import cz.cuni.mff.yaclpplib.ArgumentParserFactory;

/**
 * Entry point for the client application. Only parses parameters and runs a server instance.
 */
class Bootstrap {

    public static void main(String[] argv) {
        try {
            ArgumentParser parser = ArgumentParserFactory.create();
            ClientOptions opts = parser.addOptions(new ClientOptions());
            parser.addOptions(opts.multicastEndpointOptions);
            parser.parse(argv);

            Client client = new Client(opts);
            client.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
