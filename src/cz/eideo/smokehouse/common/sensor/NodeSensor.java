package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.api.Node;
import cz.eideo.smokehouse.common.event.EventFactory;

/**
 * Sensor with value stored in the API node.
 */
abstract public class NodeSensor<T> extends NodeSource<T> implements Sensor<T> {

    NodeSensor(Node<T> node, EventFactory eventFactory) {
        super(node, eventFactory);
    }

    @Override
    public void updateValue(T value) {
        node.setValue(value);
        signalMonitors();
    }
}
