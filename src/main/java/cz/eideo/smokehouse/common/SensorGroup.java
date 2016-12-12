package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.util.ObservableObject;
import cz.eideo.smokehouse.common.util.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Something like a virtual sensor, coupling more sensors together.
 *
 * Signals whenever a sensor signals.
 */
public class SensorGroup extends ObservableObject implements Observer {

    protected List<Sensor> sensors = new ArrayList<>();

    protected int addSensor(Sensor s) {
        s.attachObserver(this);
        sensors.add(s);
        return sensors.size() - 1;
    }

    protected Sensor getSensor(int i) {
        return sensors.get(i);
    }

    public void signal() {
        signalMonitors();
    }
}
