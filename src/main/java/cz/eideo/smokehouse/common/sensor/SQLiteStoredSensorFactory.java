package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.Sensor;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.storage.RealNumberSensorStorage;
import cz.eideo.smokehouse.common.storage.SQLiteRealNumberSensorStorage;
import cz.eideo.smokehouse.common.storage.SQLiteStorage;

import java.sql.SQLException;

public class SQLiteStoredSensorFactory extends DefaultSensorFactory {

    private final SQLiteStorage storage;

    public SQLiteStoredSensorFactory(Endpoint api, SQLiteStorage sqLiteStorage) {
        super(api);
        this.storage = sqLiteStorage;
    }

    @Override
    public Thermometer createThermometer(String name) throws FactoryException {
        Thermometer t = super.createThermometer(name);
        RealNumberSensorStorage sensorStorage = createRealStorage(t, storage.getNamespace("thermometer").getNamespace(name));
        t.setStorage(sensorStorage);
        return t;
    }

    private RealNumberSensorStorage createRealStorage(Sensor<Double> t, SQLiteStorage storage) throws FactoryException {
        try {
            return new SQLiteRealNumberSensorStorage(storage, t);
        } catch (SQLException e) {
            throw new FactoryException("Failed creating sensor storage.", e);
        }
    }
}
