package cz.eideo.smokehouse.common.storage;

import java.sql.*;

public class SQLiteStorage {

    private Connection connection;
    private String namespace;

    private final String namespaceDelimiter = "_";

    public SQLiteStorage(Connection connection, String namespace) {
        this.connection = connection;
        this.namespace = namespace;
    }

    public SQLiteStorage getNamespace(String subnamespace) {
        return new SQLiteStorage(connection, "".equals(namespace) ? subnamespace : getNamespaced(subnamespace));
    }

    protected String getNamespace() {
        return namespace;
    }

    public String getNamespaced(String identifier) {
        return String.format("%s%s%s", namespace, namespaceDelimiter, identifier);
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public static SQLiteStorage fromFile(String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Nepodařilo se načíst SQLite.");
        }

        Connection connection;
        try {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", filename));
        } catch (SQLException e) {
            throw new RuntimeException("Databáze neexistuje a nepodařilo se ji vytvořit. " + e.getMessage());
        }

        return new SQLiteStorage(connection, "");
    }

    public PreparedStatement prepareStatement(String s) throws SQLException {
        return connection.prepareStatement(s);
    }
}
