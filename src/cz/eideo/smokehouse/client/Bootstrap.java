package cz.eideo.smokehouse.client;

import cz.cuni.mff.yaclpplib.ArgumentParser;
import cz.cuni.mff.yaclpplib.ArgumentParserFactory;
import cz.cuni.mff.yaclpplib.MissingMandatoryOptionException;

/**
 * Entry point for the client application. Only parses parameters and runs a client instance.
 */
class Bootstrap {

    public static void main(String[] argv) {
        ArgumentParser parser = ArgumentParserFactory.create();
        try {
            ClientOptions opts = parser.addOptions(new ClientOptions());
            parser.parse(argv);

            Client client = new Client(opts);
            client.run();
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
