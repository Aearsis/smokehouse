package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;

public interface NodeFactory {
    /**
     * Create a node in the API Endpoint.
     * @return a node of type T
     */
    <T> Node<T> create(Codec<T> codec, String name);

    default NodeFactory getNamespace(String namespace) {
        return new NamespacedNodeFactory(this, namespace);
    }
}
