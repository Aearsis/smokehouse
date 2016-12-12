package cz.eideo.smokehouse.server;

/**
 * Entry point for the server application. Only parses parameters and runs a server instance.
 */
public class Bootstrap {

    public static void main(String[] argv) {
        try {
            ServerArguments args = ServerArguments.parseFromArgv(argv);

            Server server = new Server(args);
            server.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
