package cz.eideo.smokehouse.common.storage;

import cz.eideo.smokehouse.common.Session;
import cz.eideo.smokehouse.common.SessionStorage;
import cz.eideo.smokehouse.common.Setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Session storage backed by SQLite.
 */
public class SQLiteSessionStorage extends SessionStorage {

    private SQLiteStorage storage;

    private final int current_version = 1;

    public SQLiteSessionStorage(SQLiteStorage storage) throws SQLException, ClassNotFoundException {
        this.storage = storage;
        loadState();
    }

    private void loadState() throws SQLException, ClassNotFoundException {
        try (Statement stmt = storage.createStatement()) {

            ResultSet rs = null;
            try {
                rs = stmt.executeQuery("SELECT * FROM session_state");
            } catch (SQLException e) {
                loadDefaults();
                createDatabase();
                return;
            }

            if (!rs.next()) {
                loadDefaults();
                return;
            }

            if (rs.getInt(1) != current_version)
                throw new RuntimeException("Databáze je pro jinou verzi úložiště.");

            setState(Session.State.valueOf(rs.getString(2)));
            setSetupClass(Class.forName(rs.getString(3)));

            if (rs.next())
                throw new SQLException("Existuje více než jeden stavový řádek!");
        }
    }

    private void loadDefaults() {
        setState(Session.State.NEW);
    }

    private void createDatabase() throws SQLException {
        try (Statement stmt = storage.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS session_state (" +
                    "version INTEGER NOT NULL," +
                    "state TEXT NOT NULL," +
                    "setupClass TEXT NOT NULL," +
                    "PRIMARY KEY (version) ON CONFLICT REPLACE)");
        }

        saveState();
    }

    private final static String update_query = "INSERT INTO session_state VALUES (?, ?, ?)";

    private void saveState() throws SQLException {
        try (PreparedStatement stmt = storage.prepareStatement(update_query)) {
            stmt.setInt(1, current_version);
            stmt.setString(2, getState().toString());
            stmt.setString(3, getSetupClass().getName());
            stmt.execute();
        }
    }

    @Override
    public void flush() {
        super.flush();
        try {
            saveState();
        } catch (SQLException ignored) { }
    }
}
