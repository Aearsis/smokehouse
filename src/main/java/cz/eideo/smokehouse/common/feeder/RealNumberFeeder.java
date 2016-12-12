package cz.eideo.smokehouse.common.feeder;

import cz.eideo.smokehouse.common.Feeder;
import cz.eideo.smokehouse.common.RealNumberSensor;

/**
 * Feeder base for {@see RealValueSensor}.
 */
abstract public class RealNumberFeeder implements Feeder {

    protected RealNumberSensor sensor = null;

    public void setSensor(RealNumberSensor sensor) {
        if (this.sensor != null)
            throw new RuntimeException("Sensor already set!");
        this.sensor = sensor;
    }

}
