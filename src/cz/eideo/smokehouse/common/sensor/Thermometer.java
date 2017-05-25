package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.NodeSensor;
import cz.eideo.smokehouse.common.Sensor;
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

    private SensorStorage<Double> storage;

    public Thermometer(NodeFactory nodeFactory, EventFactory eventFactory) {
        super(nodeFactory.create(ThermalCodec.INSTANCE, "sensor" ), eventFactory);
        slidingAverage = new SlidingAverage<>(this, 16, nodeFactory, ThermalCodec.INSTANCE, eventFactory);
    }

    public void setStorage(SensorStorage<Double> storage) {
        this.storage = storage;
    }
}

