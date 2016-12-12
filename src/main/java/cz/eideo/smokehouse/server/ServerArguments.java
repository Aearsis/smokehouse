package cz.eideo.smokehouse.server;

class ServerArguments {
    String dbName;

    private ServerArguments() {}

    static ServerArguments parseFromArgv(String[] argv) {
        if (argv.length < 1)
            throw new RuntimeException("You must supply at least the file with database.");

        ServerArguments a = new ServerArguments();
        a.dbName = argv[0];
        return a;
    }
}
