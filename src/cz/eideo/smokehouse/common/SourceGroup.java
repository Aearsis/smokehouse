package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.event.Event;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.event.EventObservableObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Something like a virtual sensor, coupling more sensors together.
 * <p>
 * Signals whenever a sensor signals. Because we often do updates on whole group at once,
 * there is a batch update API.
 */
public class SourceGroup<T extends Source<?>> extends EventObservableObject implements Iterable<T> {

    private final List<T> sensors = new ArrayList<>();

    private final Event signalEvent;

    public SourceGroup(EventFactory eventFactory) {
        signalEvent = eventFactory.createEvent(this::signalMonitors);
    }

    protected int addSource(T s) {
        s.attachObserver(signalEvent);
        sensors.add(s);
        return sensors.size() - 1;
    }

    public T getSource(int i) {
        return sensors.get(i);
    }

    @Override
    public Iterator<T> iterator() {
        return sensors.iterator();
    }

    public int size() {
        return sensors.size();
    }
}
