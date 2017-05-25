package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.SourceGroup;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.ThermalCodec;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.setup.ThermometerArray;
import cz.eideo.smokehouse.common.statistics.GroupAverage;
import cz.eideo.smokehouse.common.statistics.SlidingAverage;

/**
 * Some Area of Thermometers.
 */
public class ThermoArea extends SourceGroup<Thermometer> implements ThermometerArray {

    public final GroupAverage<Double> average;
    public final SlidingAverage<Double> slidingAverage;

    public ThermoArea(NodeFactory nodeFactory, EventFactory eventFactory) {
        super(eventFactory);
        NodeFactory namespacedNodeFactory = nodeFactory.getNamespace("thermo area");
        average = new GroupAverage<>(this, namespacedNodeFactory, ThermalCodec.INSTANCE, eventFactory);
        slidingAverage = new SlidingAverage<>(average, 16, namespacedNodeFactory, ThermalCodec.INSTANCE, eventFactory);
    }

    public int addThermometer(Thermometer t) {
        return addSource(t);
    }

    public Thermometer getThermometer(int i) {
        return getSource(i);
    }


}
