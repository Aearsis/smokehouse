package cz.eideo.smokehouse.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic functionality of observable class.
 */
public class ObservableObject implements Observable {

    private final List<Observer> monitors = new ArrayList<>();

    public void attachObserver(Observer monitor) {
        monitors.add(monitor);
        monitor.attached(this);
    }

    protected void signalMonitors() {
        for (Observer m : monitors)
            m.handleSignal();
    }

}
