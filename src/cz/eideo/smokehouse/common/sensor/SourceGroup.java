package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.event.Event;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.event.EventObservableObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Something like a virtual sensor, coupling more sensors together.
 * <p>
 * Signals whenever a sensor signals.
 */
public class SourceGroup<T extends Source<?>> extends EventObservableObject implements Iterable<T> {

    private final List<T> sensors = new ArrayList<>();

    private final Event signalEvent;

    SourceGroup(EventFactory eventFactory) {
        signalEvent = eventFactory.createEvent(this::signalMonitors);
    }

    void addSource(T s) {
        s.attachObserver(signalEvent);
        sensors.add(s);
    }

    T getSource(int i) {
        return sensors.get(i);
    }

    @Override
    public Iterator<T> iterator() {
        return sensors.iterator();
    }
}
