package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.NodeSensor;
import cz.eideo.smokehouse.common.Sensor;
import cz.eideo.smokehouse.common.NodeSource;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.Node;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.ThermalCodec;
import cz.eideo.smokehouse.common.statistics.SlidingAverage;
import cz.eideo.smokehouse.common.storage.SensorStorage;

import java.time.Instant;


/**
 * For probably eternity, this will be the only real sensor :)
 */
public class Thermometer extends NodeSensor<Double> implements Sensor<Double> {

    public final SlidingAverage<Double> slidingAverage;

    private SensorStorage<Double> storage;

    public Thermometer(NodeFactory nodeFactory) {
        super(nodeFactory.create(ThermalCodec.INSTANCE, "sensor" ));
        slidingAverage = new SlidingAverage<>(this, nodeFactory, ThermalCodec.INSTANCE, 16);
    }

    public void setStorage(SensorStorage<Double> storage) {
        this.storage = storage;
    }
}

