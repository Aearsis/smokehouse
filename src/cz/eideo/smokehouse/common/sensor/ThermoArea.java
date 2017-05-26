package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.ThermalCodec;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.statistics.GroupAverage;
import cz.eideo.smokehouse.common.statistics.SlidingAverage;

/**
 * An Area of Thermometers.
 */
public class ThermoArea extends SourceGroup<Thermometer> {

    public final GroupAverage<Double> average;
    public final SlidingAverage<Double> slidingAverage;

    ThermoArea(NodeFactory nodeFactory, EventFactory eventFactory) {
        super(eventFactory);
        NodeFactory namespacedNodeFactory = nodeFactory.getNamespace("thermo area");
        average = new GroupAverage<>(this, namespacedNodeFactory, ThermalCodec.INSTANCE, eventFactory);
        slidingAverage = new SlidingAverage<>(average, 16, namespacedNodeFactory, ThermalCodec.INSTANCE, eventFactory);
    }

    public void addThermometer(Thermometer t) {
        addSource(t);
    }

    public Thermometer getThermometer(int i) {
        return getSource(i);
    }

}
