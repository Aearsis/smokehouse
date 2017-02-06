package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.SourceGroup;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.codec.ThermalCodec;
import cz.eideo.smokehouse.common.statistics.GroupAverage;
import cz.eideo.smokehouse.common.statistics.SlidingAverage;
import cz.eideo.smokehouse.common.util.Observer;

/**
 * Some Area of Thermometers.
 */
public class ThermoArea extends SourceGroup<Double> implements Observer {

    public GroupAverage average;
    public SlidingAverage slidingAverage;

    public ThermoArea(Endpoint API) {
        average = new GroupAverage(API, new ThermalCodec(), this);
        slidingAverage = new SlidingAverage(average, API, 16);
    }

    public int addThermometer(Thermometer t) {
        return addSource(t);
    }

    public Thermometer getThermometer(int i) {
        return (Thermometer) getSource(i);
    }



}
