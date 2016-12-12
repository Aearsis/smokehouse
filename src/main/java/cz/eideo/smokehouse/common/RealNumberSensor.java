package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.util.ObservableObject;

/**
 * Simple sensor, which value is a real number.
 */
abstract public class RealNumberSensor extends ObservableObject implements Sensor {

    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
        signalMonitors();
    }
}
