package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.ThermalCodec;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.statistics.SlidingAverage;
import cz.eideo.smokehouse.common.storage.SensorStorage;


/**
 * For probably eternity, this will be the only real sensor :)
 */
public class Thermometer extends NodeSensor<Double> implements Sensor<Double> {

    public final SlidingAverage<Double> slidingAverage;

    /**
     * So far, this is not used at all. The sensor storage is connected as an observer,
     * so the values are already stored. But, there must be some API in the future to
     * enable access to historic values, which will use this storage to access recorded values.
     */
    private SensorStorage<Double> storage;

    Thermometer(NodeFactory nodeFactory, EventFactory eventFactory) {
        super(nodeFactory.create(ThermalCodec.INSTANCE, "sensor" ), eventFactory);
        slidingAverage = new SlidingAverage<>(this, 16, nodeFactory, ThermalCodec.INSTANCE, eventFactory);
    }

    public void setStorage(SensorStorage<Double> storage) {
        this.storage = storage;
    }
}

