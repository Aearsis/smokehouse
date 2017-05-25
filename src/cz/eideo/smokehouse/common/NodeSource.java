package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.Node;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.event.EventObservableObject;

import java.util.Optional;

/**
 * Class with value stored inside API node.
 *
 * @param <T>
 */
abstract public class NodeSource<T> extends EventObservableObject implements Source<T> {

    protected final Node<T> node;


    public NodeSource(Node<T> node, EventFactory eventFactory) {
        this.node = node;
        node.attachObserver(eventFactory.createEvent(this::signalMonitors));
    }

    @Override
    public T waitForValue() {
        return node.waitForValue();
    }

    @Override
    public Optional<T> getValue() {
        return node.getImmediateValue();
    }
}

