package cz.eideo.smokehouse.common.storage;

import cz.eideo.smokehouse.common.Sensor;
import cz.eideo.smokehouse.common.event.Event;
import cz.eideo.smokehouse.common.event.EventFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

/**
 * Sensor storage backed by SQLite.
 */
public class SQLiteRealNumberSensorStorage implements SensorStorage<Double> {

    private final SQLiteStorage storage;
    private final Sensor<Double> sensor;
    private final String table_name;

    public SQLiteRealNumberSensorStorage(Sensor<Double> sensor, SQLiteStorage storage, EventFactory eventFactory) throws SQLException {
        this.storage = storage;
        this.sensor = sensor;
        table_name = "[" + storage.getNamespace() + "]";

        createTable();
        sensor.attachObserver(eventFactory.createEvent(this::signalFired));
    }

    private void createTable() throws SQLException {
        try (Statement stmt = storage.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + table_name + " (" +
                    "updated INTEGER NOT NULL," +
                    "value REAL NOT NULL" +
                    ")");
        }
    }

    private void handleUpdateValue() throws SQLException {
        String update_query = "INSERT INTO " + table_name + " VALUES (?, ?)";
        try (PreparedStatement stmt = storage.prepareStatement(update_query)) {
            stmt.setLong(1, Instant.now().getEpochSecond());
            stmt.setDouble(2, sensor.waitForValue());
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

    private void signalFired() {
        try {
            handleUpdateValue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
