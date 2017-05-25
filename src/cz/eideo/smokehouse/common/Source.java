package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.event.EventObservable;

import java.util.Optional;

/**
 * Source is the basic API unit, having its API key and values in time.
 * <p>
 * The value can be of any type, and can be either calculated from other sources (statistics, aggregators etc.),
 * or acquired from physical sensors - then they should implement the Sensor interface.
 */
public interface Source<T> extends EventObservable {

    T waitForValue();

    Optional<T> getValue();

}
