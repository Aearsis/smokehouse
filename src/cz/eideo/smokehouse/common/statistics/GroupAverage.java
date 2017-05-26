package cz.eideo.smokehouse.common.statistics;

import cz.eideo.smokehouse.common.sensor.Source;
import cz.eideo.smokehouse.common.sensor.SourceGroup;
import cz.eideo.smokehouse.common.sensor.NodeSource;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.event.EventFactory;

import java.util.Optional;


/**
 * A simple average of numeric sensors in a group.
 */
public class GroupAverage<T extends Number> extends NodeSource<Double> implements Source<Double> {

    private final SourceGroup<? extends Source<T>> group;

    public GroupAverage(SourceGroup<? extends Source<T>> group, NodeFactory nodeFactory, Codec<Double> codec, EventFactory eventFactory) {
        super(nodeFactory.create(codec, "group avg"), eventFactory);
        this.group = group;
        group.attachObserver(eventFactory.createEvent(this::updateValue));
    }

    /**
     * Event handler.
     */
    private void updateValue() {
        double value = 0;
        int count = 0;

        for (Source<T> s : group) {
            Optional<T> n = s.getValue();
            if (!n.isPresent()) continue;
            double v = n.get().doubleValue();
            value = (value * count) + v;
            value /= ++count;
        }

        if (count == 0)
            return;

        node.setValue(value);
        signalMonitors();
    }
}
