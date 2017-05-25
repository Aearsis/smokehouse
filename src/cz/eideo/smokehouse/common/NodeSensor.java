package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.Node;

/**
 * Sensor with value stored in API.
 */
public class NodeSensor<T> extends NodeSource<T> implements Sensor<T> {

    public NodeSensor(Node<T> node) {
        super(node);
    }

    @Override
    public void updateValue(T value) {
        node.setValue(value);
        signalMonitors();
    }
}
