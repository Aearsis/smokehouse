package cz.eideo.smokehouse.common.api.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Codecs define how values are transmitted in packets.
 */
public interface Codec<T> {

    void encode(T value, DataOutputStream stream) throws IOException;
    T decode(DataInputStream stream) throws IOException;

}
