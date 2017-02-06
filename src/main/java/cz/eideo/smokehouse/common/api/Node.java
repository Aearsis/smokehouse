package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Node represents a value that is read and written by the API.
 *
 * Node should use a Codec to encode its value,
 * but its unspecified where to get the value.
 */
public interface Node<T> {

    /**
     * API receiver will call this method, when new value is received.
     */
    void writeValue(DataOutputStream stream) throws IOException;

    /**
     * API provider will call this method to send it to endpoints.
     */
    void readValue(DataInputStream dataStream) throws IOException;

    Codec<T> getCodec();

    void dump();
}
