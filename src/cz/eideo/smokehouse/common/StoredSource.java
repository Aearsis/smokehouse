package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.StorageNode;
import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.util.Observer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with value stored inside API node.
 *
 * @param <T>
 */
abstract public class StoredSource<T> extends StorageNode<T> implements Source<T> {

    public StoredSource(Endpoint API, Codec<T> codec, String name) {
        super(API, codec, name);
    }

    @Override
    public T getValueInTime(Instant time) {
        return null;
    }

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
