package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Storage nodes stores their value and uses codecs for writing and reading.
 *
 * @param <T>
 */

public class LocalNode<T> implements Node<T> {

    private final Endpoint API;
    private final int key;

    private final Codec<T> codec;
    private final String name;

    private T value = null;

    final static Logger logger = Logger.getLogger(LocalNode.class.getName());

    public LocalNode(Endpoint endpoint, Codec<T> codec, String name) {
        this.codec = codec;
        this.API = endpoint;
        this.name = name;
        key = endpoint.assignKey();
        endpoint.addNode(this);
    }

    public synchronized T waitForValue() {
        return value;
    }

    public synchronized void setValue(T value) {
        try {
            // Avoid unnecessarily sending the same value
            if (value.equals(this.value))
                return;

            this.value = value;
            API.sendValue(this);
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

    /**
     * Wait until we have a value, then become a master.
     */
    @Override
    public boolean isMaster() {
        return value != null;
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
                return new LocalNode<>(endpoint, codec, name);
            }
        };
    }
}
