package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.Sensor;
import cz.eideo.smokehouse.common.StoredSource;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.codec.ThermalCodec;
import cz.eideo.smokehouse.common.statistics.SlidingAverage;
import cz.eideo.smokehouse.common.storage.SensorStorage;

import java.time.Instant;


/**
 * For probably eternity, this will be the only real sensor :)
 */
public class Thermometer extends StoredSource<Double> implements Sensor<Double> {

    public final SlidingAverage slidingAverage;
    private SensorStorage<Double> storage;

    public Thermometer(Endpoint API) {
        super(API, new ThermalCodec(), "thermometer");
        slidingAverage = new SlidingAverage(this, API, 16);
    }

    @Override
    public void updateValue(Double value) {
        setValue(value);
        signalMonitors();
    }

    @Override
    public Double getValueInTime(Instant time) {
        if (storage == null)
            return null;

        return storage.getValueInTime(time);
    }

    public void setStorage(SensorStorage<Double> storage) {
        this.storage = storage;
    }
}

