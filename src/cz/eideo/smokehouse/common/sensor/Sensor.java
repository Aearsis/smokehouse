package cz.eideo.smokehouse.common.sensor;

/**
 * Sensors are the physical sensors present in the smokehouse.
 * <p>
 * The difference from Sources are that Sensors values are to be stored into Storage.
 * Sensors value is set by the feeders.
 */
public interface Sensor<T> extends Source<T> {

    /**
     * A new value is read from the real sensor.
     *
     * @param value the current value
     */
    void updateValue(T value);
}
