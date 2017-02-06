package cz.eideo.smokehouse.common.statistics;

import cz.eideo.smokehouse.common.StorageSource;
import cz.eideo.smokehouse.common.SourceGroup;
import cz.eideo.smokehouse.common.Source;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.codec.Codec;
import cz.eideo.smokehouse.common.util.Observer;


public class GroupAverage extends StorageSource<Double> implements Source<Double>, Observer {

    SourceGroup<Double> group;

    public GroupAverage(Endpoint API, Codec<Double> codec, SourceGroup<Double> group) {
        super(API, codec, "group");
        this.group = group;
        group.attachObserver(this);
    }

    @Override
    public void signal() {
        double value = 0;
        int count = 0;

        for (Source<Double> s : group) {
            Double v = s.getValue();
            if (v == null)
                continue;
            value = (value * count) + v;
            value /= ++count;
        }

        if (count == 0)
            return;

        setValue(value);
        signalMonitors();
    }
}
