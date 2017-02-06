package cz.eideo.smokehouse.common.storage;

import cz.eideo.smokehouse.common.Sensor;
import cz.eideo.smokehouse.common.util.Observer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

/**
 * Sensor storage backed by SQLite.
 */
public class SQLiteRealNumberSensorStorage implements Observer, RealNumberSensorStorage {

    private final SQLiteStorage storage;
    private final Sensor<Double> sensor;
    private final String table_name;

    public SQLiteRealNumberSensorStorage(SQLiteStorage storage, Sensor<Double> sensor) throws SQLException {
        this.storage = storage;
        this.sensor = sensor;
        sensor.attachObserver(this);
        table_name = storage.getNamespace();

        createTable();
    }

    private void createTable() throws SQLException {
        try (Statement stmt = storage.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + table_name + " (" +
                    "updated INTEGER NOT NULL," +
                    "value REAL NOT NULL" +
                    ")");
        }
    }

    public void updateValue() throws SQLException {
        String update_query = "INSERT INTO " + table_name + " VALUES (?, ?)";
        try (PreparedStatement stmt = storage.prepareStatement(update_query)) {
            stmt.setLong(1, Instant.now().getEpochSecond());
            stmt.setDouble(2, sensor.getValue());
            stmt.execute();
        }
    }

    @Override
    public Double getValueInTime(Instant time) {
        try (PreparedStatement stmt = storage.prepareStatement("SELECT * FROM " + table_name +
                    "WHERE updated < ? ORDER BY updated DESC LIMIT 1")) {
            stmt.setLong(1, Instant.now().getEpochSecond());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                return null;

            return rs.getDouble(2);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void signal() {
        try {
            updateValue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
