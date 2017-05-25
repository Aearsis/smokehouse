package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.util.ObservableObject;
import cz.eideo.smokehouse.common.util.Observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Something like a virtual sensor, coupling more sensors together.
 * <p>
 * Signals whenever a sensor signals.
 */
public class SourceGroup<T extends Source<?>> extends ObservableObject implements Observer, Iterable<T> {

    private final List<T> sensors = new ArrayList<>();

    protected int addSource(T s) {
        s.attachObserver(this);
        sensors.add(s);
        return sensors.size() - 1;
    }

    public T getSource(int i) {
        return sensors.get(i);
    }

    public void handleSignal() {
        signalMonitors();
    }

    @Override
    public Iterator<T> iterator() {
        return sensors.iterator();
    }

    public int size() {
        return sensors.size();
    }
}
