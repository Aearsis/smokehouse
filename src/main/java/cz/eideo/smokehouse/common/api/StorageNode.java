package cz.eideo.smokehouse.common.api;

import java.time.Instant;

abstract public class StorageNode<T> implements Node<T> {

    protected T value = null;
    private Instant validBefore = Instant.EPOCH;

    private static final long validMillis = 3000;

    private Endpoint API = BlackHole.SINGULARITY;
    private int key;

    protected StorageNode() { }

    public void attachAPI(Endpoint API) {
        this.API = API;
        key = API.addNode(this);
    }

    public synchronized T getValue() {
        while (validBefore.compareTo(Instant.now()) <= 0
                && API.announceQueryValue(key))
            try {
                wait(1000);
            } catch (InterruptedException ignored) { }

        return value;
    }

    public void setValue(T value) {
        setValue(value, validMillis);
    }

    public synchronized void setValue(T value, long validMillis) {
        this.value = value;
        this.validBefore = Instant.now().plusMillis(validMillis);

        API.announceValueChanged(key);

        notifyAll();
    }

}
