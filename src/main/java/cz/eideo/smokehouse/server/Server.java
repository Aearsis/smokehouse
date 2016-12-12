package cz.eideo.smokehouse.server;

import cz.eideo.smokehouse.common.Session;
import cz.eideo.smokehouse.common.SessionStorage;
import cz.eideo.smokehouse.common.Setup;
import cz.eideo.smokehouse.common.storage.*;
import sun.misc.Signal;
import java.sql.SQLException;

/**
 * Server instance. It initializes the environment for running server.
 *
 * Session name is supplied on start. Server has three responsibilities:
 *      1) Intialize feeders according to the HW
 *      2) Load the session, give it storage and connect feeders
 *      3) Provide network API to the session
 */
class Server {

    private final Session session;

    Server (ServerArguments args) {
        try {
            SessionStorage storage = new SessionSQLiteStorage(SQLiteStorage.fromFile(args.dbName));
            session = new Session(storage);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Nenalezena odpovídající konfigurace udírny.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Nezdařila se inicializace serveru: " + e.getMessage(), e);
        }
    }

    void run() {
        session.loadSetup();
        Setup s = session.getSetup();
        s.setupCommon();
        s.setupServer();

        s.startServer();

        Signal.handle(new Signal("TERM"), signal -> s.stopServer());
    }
}

