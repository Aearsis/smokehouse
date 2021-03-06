package cz.eideo.smokehouse.common.statistics;

import cz.eideo.smokehouse.common.sensor.Source;
import cz.eideo.smokehouse.common.sensor.NodeSource;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.event.EventFactory;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An average of last windowSize values announced.
 */
public class SlidingAverage<T extends Number> extends NodeSource<Double> implements Source<Double> {

    private final Source<T> sensor;

    /**
     * How many values to consider.
     */
    private final int windowSize;

    /**
     * A ring-buffer of old values.
     */
    private final Deque<Double> historicValues;

    public SlidingAverage(Source<T> sensor, int windowSize, NodeFactory nodeFactory, Codec<Double> codec, EventFactory eventFactory) {
        super(nodeFactory.create(codec, "sliding avg (" + windowSize + ")"), eventFactory);
        this.sensor = sensor;
        this.windowSize = windowSize;
        historicValues = new ArrayDeque<>(windowSize);
        node.setValue(0d);
        sensor.attachObserver(eventFactory.createEvent(this::updateValue));
    }

    /**
     * Event handler.
     */
    private void updateValue() {
        double current = sensor.waitForValue().doubleValue();
        if (historicValues.size() == windowSize) {
            Double removed = historicValues.remove();
            node.setValue(node.waitForValue() + (current - removed) / windowSize);
            historicValues.add(current);
        } else {
            double sum = waitForValue() * historicValues.size();
            historicValues.add(current);
            sum += current;
            node.setValue(sum / historicValues.size());
        }
        signalMonitors();
    }

}
