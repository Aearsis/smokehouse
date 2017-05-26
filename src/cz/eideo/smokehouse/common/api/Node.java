package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.event.EventObservable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * Node represents a value that is shared by the API.
 *
 * It always has a value, though it need not to be a "real" value.
 */
public interface Node<T> extends EventObservable {

    /**
     * Get the key assigned to this node.
     */
    int getApiKey();

    /**
     * Get the Endpoint this node is associated with.
     */
    Endpoint getEndpoint();

    /**
     * Wait until the value is available, then get it - the user wants it.
     */
    T waitForValue();

    /**
     * Store the current value to node - the user changed it.
     */
    void setValue(T value);

    /**
     * Get the current value from node - the API wants it.
     */
    Optional<T> getImmediateValue();

    /**
     * Set the current value to node - the API received it.
     */
    void setValueFromApi(T value);

    /**
     * @return Whether this node wants its value to be sent.
     * There should be only one node of this key being master in the whole network,
     * otherwise weird things will happen.
     */
    boolean isMaster();

    /**
     * API receiver will call this method, when new value is received.
     *
     * But why?
     * Java type system is too weak to infer T outside of Node...
     */
    default void writeValueTo(DataOutputStream stream) throws IOException {
        Optional<T> valueForApi = getImmediateValue();
        if (valueForApi.isPresent())
            getCodec().encode(valueForApi.get(), stream);
        else
            getCodec().encode(null, stream);
    }

    /**
     * API provider will call this method to send it to endpoints.
     */
    default void readValueFrom(DataInputStream dataStream) throws IOException {
        setValueFromApi(getCodec().decode(dataStream));
    }

    Codec<T> getCodec();
}
