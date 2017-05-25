package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.Node;
import cz.eideo.smokehouse.common.util.ObservableObject;

import java.util.Optional;

/**
 * Class with value stored inside API node.
 *
 * @param <T>
 */
abstract public class NodeSource<T> extends ObservableObject implements Source<T> {

    protected final Node<T> node;

    public NodeSource(Node<T> node) {
        this.node = node;
    }

    @Override
    public T waitForValue() {
        return node.waitForValue();
    }

    @Override
    public void queryValue() {
        node.getEndpoint().sendQuery(node);
    }

    @Override
    public Optional<T> getValue() {
        return node.getImmediateValue();
    }
}

