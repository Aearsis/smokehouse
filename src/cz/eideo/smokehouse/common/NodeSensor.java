package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.Node;
import cz.eideo.smokehouse.common.event.EventFactory;

/**
 * Sensor with value stored in API.
 */
abstract public class NodeSensor<T> extends NodeSource<T> implements Sensor<T> {

    public NodeSensor(Node<T> node, EventFactory eventFactory) {
        super(node, eventFactory);
    }

    @Override
    public void updateValue(T value) {
        node.setValue(value);
        signalMonitors();
    }
}
