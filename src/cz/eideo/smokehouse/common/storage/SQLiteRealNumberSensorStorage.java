package cz.eideo.smokehouse.common.storage;

import cz.eideo.smokehouse.common.sensor.Sensor;
import cz.eideo.smokehouse.common.event.EventFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A storage for a real value sensor backed by SQLite.
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
        sensor.attachObserver(eventFactory.createEvent(this::storeCurrentValue));
    }

    /**
     * Initialize the storage.
     * @throws SQLException on db error
     */
    private void createTable() throws SQLException {
        try (Statement stmt = storage.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + table_name + " (" +
                    "updated INTEGER NOT NULL," +
                    "value REAL NOT NULL" +
                    ")");
        }
    }

    /**
     * Event handler - store the current value.
     */
    private void storeCurrentValue() {
        String update_query = "INSERT INTO " + table_name + " VALUES (?, ?)";
        try (PreparedStatement stmt = storage.prepareStatement(update_query)) {
            stmt.setLong(1, Instant.now().getEpochSecond());
            stmt.setDouble(2, sensor.waitForValue());
            stmt.execute();
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to store value: " + e.getMessage(), e);
        }
    }

    /**
     * Find the latest value in the time given.
     *
     * @param time time in which to query for value
     * @return the value in time
     */
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
}
