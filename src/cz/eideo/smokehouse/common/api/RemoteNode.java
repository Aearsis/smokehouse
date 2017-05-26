package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.event.EventObservableObject;

import java.util.Optional;

/**
 * The RemoteNode is reflecting the value being shared by the LocalNode.
 */

public class RemoteNode<T> extends EventObservableObject implements Node<T> {

    private final Endpoint endpoint;
    private final int key;
    private final Codec<T> codec;

    private Optional<T> value = Optional.empty();

    private RemoteNode(Endpoint endpoint, Codec<T> codec, String name) {
        this.codec = codec;
        this.endpoint = endpoint;
        key = endpoint.assignKey();
        endpoint.addNode(this);
        endpoint.sendQuery(this);
    }

    @Override
    public synchronized T waitForValue() {
        while (!value.isPresent()) {
            try {
                endpoint.sendQuery(this);
                endpoint.flushQueue();
                // We really need the value, so spam the network -
                // but remember we need to receive the response :)
                wait(300);
            } catch (InterruptedException ignored) { }
        }

        return value.get();
    }

    @Override
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

    public static NodeFactory createNodeFactory(final Endpoint endpoint) {
        return new NodeFactory() {
            @Override
            public <T> Node<T> create(Codec<T> codec, String name) {
                return new RemoteNode<>(endpoint, codec, name);
            }
        };
    }
}
