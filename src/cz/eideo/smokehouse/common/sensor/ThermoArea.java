package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.SourceGroup;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.ThermalCodec;
import cz.eideo.smokehouse.common.setup.ThermometerArray;
import cz.eideo.smokehouse.common.statistics.GroupAverage;
import cz.eideo.smokehouse.common.statistics.SlidingAverage;
import cz.eideo.smokehouse.common.util.Observer;

/**
 * Some Area of Thermometers.
 */
public class ThermoArea extends SourceGroup<Thermometer> implements Observer, ThermometerArray {

    public final GroupAverage<Double> average;
    public final SlidingAverage<Double> slidingAverage;

    public ThermoArea(NodeFactory nodeFactory) {
        NodeFactory namespacedFactory = nodeFactory.getNamespace("thermo area");
        average = new GroupAverage<>(this, namespacedFactory, ThermalCodec.INSTANCE);
        slidingAverage = new SlidingAverage<>(average, namespacedFactory, ThermalCodec.INSTANCE, 16);
    }

    public int addThermometer(Thermometer t) {
        return addSource(t);
    }

    public Thermometer getThermometer(int i) {
        return (Thermometer) getSource(i);
    }


}
