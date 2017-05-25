package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.util.ObservableObject;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Storage nodes stores their value and uses codecs for writing and reading.
 *
 * @param <T>
 */

public class RemoteNode<T> extends ObservableObject implements Node<T> {

    private final Endpoint endpoint;
    private final int key;

    private final Codec<T> codec;
    private final String name;

    private Optional<T> value = Optional.empty();

    final static Logger logger = Logger.getLogger(RemoteNode.class.getName());

    public RemoteNode(Endpoint endpoint, Codec<T> codec, String name) {
        this.codec = codec;
        this.endpoint = endpoint;
        this.name = name;
        key = endpoint.assignKey();
        endpoint.addNode(this);
        endpoint.sendQuery(this);
    }

    public synchronized T waitForValue() {
        while (!value.isPresent()) {
            try {
                endpoint.sendQuery(this);
                // We really need the value, so spam the network -
                // but remember we need to receive the response :)
                wait(100);
            } catch (InterruptedException ignored) { }
        }

        return value.get();
    }

    public synchronized void setValue(T value) {
        if (value.equals(this.value))
            return;

        this.value = Optional.of(value);
        notifyAll();
        signalMonitors();
    }

    @Override
    public int getApiKey() {
        return key;
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public Optional<T> getImmediateValue() {
        logger.log(Level.WARNING, "Remote API node value is read from API.");
        return value;
    }

    @Override
    public void setValueFromApi(T value) {
        setValue(value);
    }

    @Override
    public boolean isMaster() {
        return false;
    }

    public Codec<T> getCodec() {
        return codec;
    }

    @Override
    public void dump() {
        System.err.print(name + " = ");
        System.err.println(value);
    }

    public static NodeFactory createNodeFactory(final Endpoint endpoint) {
        return new NodeFactory() {
            @Override
            public <T> Node<T> create(Codec<T> codec, String name) {
                return new RemoteNode<>(endpoint, codec, name);
            }
        };
    }
}
