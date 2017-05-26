package cz.eideo.smokehouse.common.storage;

import java.io.File;
import java.sql.*;

/**
 * A poor-man's database wrapper.
 */
public class SQLiteStorage {

    private final Connection connection;
    private final String namespace;

    private SQLiteStorage(Connection connection, String namespace) {
        this.connection = connection;
        this.namespace = namespace;
    }

    /**
     * Get isolated area. The other storage clases reusing SQLiteStorage
     * shall prefix their tables by the namespace.
     *
     * @param subnamespace the namespace identifier
     * @return namespaced storage
     */
    public SQLiteStorage getNamespace(String subnamespace) {
        return new SQLiteStorage(connection, "".equals(namespace) ? subnamespace : getNamespacedIdentifier(subnamespace));
    }

    /**
     * @return current absolute namespace
     */
    String getNamespace() {
        return namespace;
    }

    /**
     * Construct a derived identifier combining it with the namespace
     *
     * @param identifier an input identifier
     * @return altered identifier, unique for every unique pair (namespace, identifier)
     */
    String getNamespacedIdentifier(String identifier) {
        return String.format("%s/%s", namespace, identifier.replaceAll("/", "_"));
    }

    Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public static SQLiteStorage fromFile(File file) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Nepodařilo se načíst SQLite.");
        }

        Connection connection;
        try {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", file.getPath()));
        } catch (SQLException e) {
            throw new RuntimeException("Databáze neexistuje a nepodařilo se ji vytvořit. " + e.getMessage());
        }

        return new SQLiteStorage(connection, "");
    }

    PreparedStatement prepareStatement(String s) throws SQLException {
        return connection.prepareStatement(s);
    }
}
