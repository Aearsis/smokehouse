package cz.eideo.smokehouse.common.statistics;

import cz.eideo.smokehouse.common.Source;
import cz.eideo.smokehouse.common.StoredSource;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.util.Observer;

import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingAverage extends StoredSource<Double> implements Source<Double>, Observer {

    private final Source<Double> sensor;

    /**
     * How many values to consider
     */
    private final int windowSize;

    private final Deque<Double> historicValues;

    public SlidingAverage(Source<Double> sensor, Endpoint API, int windowSize) {
        super(API, sensor.getCodec(), "sliding avg (" + windowSize + ")");
        this.sensor = sensor;
        this.windowSize = windowSize;
        historicValues = new ArrayDeque<>(windowSize);
        sensor.attachObserver(this);
        setValue(0d);
    }

    @Override
    public void handleSignal() {
        double current = sensor.getValue();
        if (historicValues.size() == windowSize) {
            double removed = historicValues.remove();
            setValue(getValue() + (current - removed) / windowSize);
            historicValues.add(current);
        } else {
            double sum = getValue() * historicValues.size();
            historicValues.add(current);
            sum += current;
            setValue(sum / historicValues.size());
        }
        signalMonitors();
    }

}
