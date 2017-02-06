package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.StorageSource;
import cz.eideo.smokehouse.common.Sensor;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.codec.ThermalCodec;
import cz.eideo.smokehouse.common.statistics.SlidingAverage;


/**
 * For probably eternity, this will be the only real sensor :)
 */
public class Thermometer extends StorageSource<Double> implements Sensor<Double> {

    public SlidingAverage slidingAverage;

    public Thermometer(Endpoint API) {
        super(API, new ThermalCodec(), "thermometer");
        slidingAverage = new SlidingAverage(this, API, 16);
    }

    @Override
    public void updateValue(Double value) {
        setValue(value);
        signalMonitors();
    }
}

