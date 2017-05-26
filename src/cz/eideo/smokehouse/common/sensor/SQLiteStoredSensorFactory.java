package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.storage.SensorStorage;
import cz.eideo.smokehouse.common.storage.SQLiteRealNumberSensorStorage;
import cz.eideo.smokehouse.common.storage.SQLiteStorage;

import java.sql.SQLException;

/**
 * A {@link SensorFactory}, that stores sensor's values in the SQLiteStorage.
 */
public class SQLiteStoredSensorFactory extends DefaultSensorFactory {

    private final SQLiteStorage storage;

    public SQLiteStoredSensorFactory(NodeFactory nodeFactory, EventFactory eventFactory, SQLiteStorage sqLiteStorage) {
        super(nodeFactory, eventFactory);
        this.storage = sqLiteStorage;
    }

    @Override
    public Thermometer createThermometer(String name) throws FactoryException {
        Thermometer t = super.createThermometer(name);
        SensorStorage<Double> sensorStorage = createRealStorage(t, storage.getNamespace("thermometer").getNamespace(name));
        t.setStorage(sensorStorage);
        return t;
    }

    private SensorStorage<Double> createRealStorage(Sensor<Double> t, SQLiteStorage storage) throws FactoryException {
        try {
            return new SQLiteRealNumberSensorStorage(t, storage, eventFactory);
        } catch (SQLException e) {
            throw new FactoryException("Failed creating sensor storage.", e);
        }
    }
}
