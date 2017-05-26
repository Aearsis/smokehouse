package cz.eideo.smokehouse.common.api.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Codecs define how values are transmitted in packets.
 */
public interface Codec<T> {

    /**
     * Take the value and write it on the stream.
     * @throws IOException when there is an error accessing the stream
     */
    void encode(T value, DataOutputStream stream) throws IOException;

    /**
     * Take the value from the stream.
     * @throws IOException when there is an error accessing the stream
     */
    T decode(DataInputStream stream) throws IOException;

}
