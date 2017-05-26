package cz.eideo.smokehouse.common.statistics;

import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.sensor.NodeSource;
import cz.eideo.smokehouse.common.sensor.Source;

/**
 * An average of last windowSize values announced.
 */
public class ExponentialAverage<T extends Number> extends NodeSource<Double> implements Source<Double> {

    private final Source<T> sensor;

    /**
     * A double from [0, 1].
     * The closer to 1, the faster will the value change.
     * 0 - keeps the initial value
     * 1 - always contains the last value
     */
    private final double coef;

    public ExponentialAverage(Source<T> sensor, double coef, NodeFactory nodeFactory, Codec<Double> codec, EventFactory eventFactory) {
        super(nodeFactory.create(codec, "exponential avg (" + coef + ")"), eventFactory);
        this.sensor = sensor;
        this.coef = coef;
        node.setValue(0d);
        sensor.attachObserver(eventFactory.createEvent(this::updateValue));
    }

    /**
     * Event handler.
     */
    private void updateValue() {
        double current = sensor.waitForValue().doubleValue();
        node.setValue(node.waitForValue() * (1 - coef) + current * coef);
        signalMonitors();
    }

}
