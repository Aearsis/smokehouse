package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;

/**
 * A NodeFactory is a single interface to create both Local and Remote nodes.
 */
public interface NodeFactory {

    /**
     * Create a node in the API Endpoint.
     * @param <T> type of value the Node has
     * @param name a name of this node for dumping purposes
     * @param codec Codec to use to en/decode the value
     * @return a node of type T
     */
    <T> Node<T> create(Codec<T> codec, String name);

    /**
     * See {@link NamespacedNodeFactory}.
     * @param namespace arbitrary string
     * @return functionally the same factory
     */
    default NodeFactory getNamespace(String namespace) {
        return new NamespacedNodeFactory(this, namespace);
    }
}
