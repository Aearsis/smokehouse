package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;

/**
 * Storage nodes stores their value and uses codecs for writing and reading.
 *
 * @param <T>
 */

public class StorageNode<T> implements Node<T> {

    private T value = null;
    private Instant validBefore = Instant.EPOCH;

    private static final long defaultValidMillis = 3000;

    private final Endpoint API;
    private final int key;

    private Codec<T> codec;

    private final String name;

    public StorageNode(Endpoint API, Codec<T> codec, String name) {
        this.codec = codec;
        this.API = API;
        this.name = name;
        key = API.addNode(this);
    }

    public synchronized T getValue() {
        while (validBefore.compareTo(Instant.now()) <= 0
                && API.announceQueryValue(key))
            try {
                wait(1000);
            } catch (InterruptedException ignored) {
            }

        return value;
    }

    public void setValue(T value) {
        setValue(value, defaultValidMillis);
    }

    public synchronized void setValue(T value, long validMillis) {
        this.value = value;
        this.validBefore = Instant.now().plusMillis(validMillis);
        API.announceValueChanged(key);
        notifyAll();
    }

    public Codec<T> getCodec() {
        return codec;
    }
    public void setCodec(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public void dump() {
        System.err.print(name + " = ");
        System.err.println(value);
    }

    @Override
    public void writeValueTo(DataOutputStream stream) throws IOException {
        codec.encode(value, stream);
    }

    @Override
    public void readValueFrom(DataInputStream stream) throws IOException {
        setValue(codec.decode(stream));
    }
}
