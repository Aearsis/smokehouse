package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.sensor.Thermometer;
import cz.eideo.smokehouse.common.util.Observer;

/**
 * Some Area of Thermometers.
 */
public class ThermoArea extends SensorGroup implements Observer {

    public int addThermometer(Thermometer t) {
        return addSensor(t);
    }

    public Thermometer getThermometer(int i) {
        return (Thermometer) getSensor(i);
    }

}
