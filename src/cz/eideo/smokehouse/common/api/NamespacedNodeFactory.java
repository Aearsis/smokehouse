package cz.eideo.smokehouse.common.api;

import cz.eideo.smokehouse.common.api.codec.Codec;

/**
 * Simple namespacing node factory decorator.
 */
public class NamespacedNodeFactory implements NodeFactory {

    final NodeFactory decorated;
    final String namespace;

    public NamespacedNodeFactory(NodeFactory decorated, String namespace) {
        this.decorated = decorated;
        this.namespace = namespace;
    }

    @Override
    public <T> Node<T> create(Codec<T> codec, String name) {
        return decorated.create(codec, namespace + " / " + name);
    }

}
