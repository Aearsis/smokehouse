package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.util.ObservableObject;
import cz.eideo.smokehouse.common.util.Observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Something like a virtual sensor, coupling more sensors together.
 *
 * Signals whenever a sensor signals.
 */
public class SourceGroup<T> extends ObservableObject implements Observer, Iterable<Source<T>> {

    protected List<Source<T>> sensors = new ArrayList<>();

    protected int addSource(Source<T> s) {
        s.attachObserver(this);
        sensors.add(s);
        return sensors.size() - 1;
    }

    public Source<T> getSource(int i) {
        return sensors.get(i);
    }

    public void signal() {
        signalMonitors();
    }

    @Override
    public Iterator<Source<T>> iterator() {
        return sensors.iterator();
    }

    public int size() {
        return sensors.size();
    }
}
