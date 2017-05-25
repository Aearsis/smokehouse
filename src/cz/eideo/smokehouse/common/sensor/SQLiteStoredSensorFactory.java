package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.Sensor;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.storage.SensorStorage;
import cz.eideo.smokehouse.common.storage.SQLiteRealNumberSensorStorage;
import cz.eideo.smokehouse.common.storage.SQLiteStorage;

import java.sql.SQLException;

public class SQLiteStoredSensorFactory extends DefaultSensorFactory {

    private final SQLiteStorage storage;

    public SQLiteStoredSensorFactory(NodeFactory factory, SQLiteStorage sqLiteStorage) {
        super(factory);
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
            return new SQLiteRealNumberSensorStorage(storage, t);
        } catch (SQLException e) {
            throw new FactoryException("Failed creating sensor storage.", e);
        }
    }
}
