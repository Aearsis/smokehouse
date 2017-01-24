package cz.eideo.smokehouse.common;

/**
 * Feeder base for {@see RealValueSensor}.
 */
abstract public class RealNumberFeeder implements Feeder {

    protected RealNumberSource sensor = null;

    public void setSensor(RealNumberSource sensor) {
        if (this.sensor != null)
            throw new RuntimeException("Sensor already set!");
        this.sensor = sensor;
    }

}
