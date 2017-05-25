package cz.eideo.smokehouse.common.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic functionality of observable class.
 */
public class EventObservableObject implements EventObservable {

    private final List<Event> monitors = new ArrayList<>();

    public void attachObserver(Event signalEvent) {
        monitors.add(signalEvent);
    }

    protected void signalMonitors() {
        for (Event e : monitors)
            e.schedule();
    }

}
