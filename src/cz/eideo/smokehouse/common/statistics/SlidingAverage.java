package cz.eideo.smokehouse.common.statistics;

import cz.eideo.smokehouse.common.Source;
import cz.eideo.smokehouse.common.NodeSource;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.util.Observer;

import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingAverage<T extends Number> extends NodeSource<Double> implements Source<Double>, Observer {

    private final Source<T> sensor;

    /**
     * How many values to consider
     */
    private final int windowSize;

    private final Deque<Double> historicValues;

    public SlidingAverage(Source<T> sensor, NodeFactory nodeFactory, Codec<Double> codec, int windowSize) {
        super(nodeFactory.create(codec, "sliding avg (" + windowSize + ")"));
        this.sensor = sensor;
        this.windowSize = windowSize;
        historicValues = new ArrayDeque<>(windowSize);
        sensor.attachObserver(this);
        node.setValue(0d);
    }

    @Override
    public void handleSignal() {
        double current = sensor.waitForValue().doubleValue();
        if (historicValues.size() == windowSize) {
            Double removed = historicValues.remove();
            node.setValue(waitForValue() + (current - removed) / windowSize);
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
