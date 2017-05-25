package cz.eideo.smokehouse.common.statistics;

import cz.eideo.smokehouse.common.Source;
import cz.eideo.smokehouse.common.SourceGroup;
import cz.eideo.smokehouse.common.NodeSource;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.util.Observer;


public class GroupAverage<T extends Number> extends NodeSource<Double> implements Source<Double>, Observer {

    private final SourceGroup<? extends Source<T>> group;

    public GroupAverage(SourceGroup<? extends Source<T>> group, NodeFactory nodeFactory, Codec<Double> codec) {
        super(nodeFactory.create(codec, "group avg"));
        this.group = group;
        group.attachObserver(this);
    }

    @Override
    public void handleSignal() {
        double value = 0;
        int count = 0;

        for (Source<T> s : group) {
            Number n = s.waitForValue();
            if (n == null) continue;
            double v = n.doubleValue();
            value = (value * count) + v;
            value /= ++count;
        }

        if (count == 0)
            return;

        node.setValue(value);
        signalMonitors();
    }
}
