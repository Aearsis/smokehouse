package cz.eideo.smokehouse.client;

/**
 * Entry point for the client application. Only parses parameters and runs a server instance.
 */
public class Bootstrap {

    public static void main(String[] argv) {
        try {
            ClientArguments args = ClientArguments.parseFromArgv(argv);

            Client client = new Client(args);
            client.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
