package cz.eideo.smokehouse.common.storage;

import java.time.Instant;

/**
 * Storage for saving real values.
 * Updates are propagated by means of observing.
 */
public interface RealNumberSensorStorage {

    Double getValueInTime(Instant time);

}
