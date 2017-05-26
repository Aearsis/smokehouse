package cz.eideo.smokehouse.common.storage;

import java.time.Instant;

/**
 * Storage for saving real values.
 * Updates are propagated by means of observing.
 */
public interface SensorStorage<T> {

    /**
     * Not yet used.
     *
     * @param time time in which to query for value
     * @return value, that the sensor had in time
     */
    T getValueInTime(Instant time);

}
