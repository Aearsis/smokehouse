package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.event.EventObservableObject;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The LocalNode is the master, owning the value being shared through the API.
 */
public class LocalNode<T> extends EventObservableObject implements Node<T> {

    private final static Logger logger = Logger.getLogger(LocalNode.class.getName());

    private final Endpoint API;
    private final int key;
    private final Codec<T> codec;

    private T value = null;

    private LocalNode(Endpoint endpoint, Codec<T> codec, String name) {
        this.codec = codec;
        this.API = endpoint;
        key = endpoint.assignKey();
        endpoint.addNode(this);
    }

    @Override
    public synchronized T waitForValue() {
        return value;
    }

    @Override
    public synchronized void setValue(T value) {
        try {
            // Avoid unnecessarily sending the same value
            if (value.equals(this.value))
                return;

            this.value = value;
            API.sendValue(this);
            signalMonitors();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to send value to API: " + e.getMessage());
        }
    }

    @Override
    public int getApiKey() {
        return key;
    }

    @Override
    public Endpoint getEndpoint() {
        return API;
    }

    @Override
    public Optional<T> getImmediateValue() {
        return Optional.of(value);
    }

    @Override
    public void setValueFromApi(T value) {
        // ignore messages sent to ourselves
    }

    @Override
    public boolean isMaster() {
        // Wait until we have a value, then become a master.
        return value != null;
    }

    public Codec<T> getCodec() {
        return codec;
    }

    public static NodeFactory createNodeFactory(final Endpoint endpoint) {
        return new NodeFactory() {
            @Override
            public <T> Node<T> create(Codec<T> codec, String name) {
                return new LocalNode<>(endpoint, codec, name);
            }
        };
    }
}
