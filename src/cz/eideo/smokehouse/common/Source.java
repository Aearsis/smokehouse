package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.util.Observable;

import java.util.Optional;

/**
 * Source is the basic API unit, having its API key and values in time.
 * <p>
 * The value can be of any type, and can be either calculated from other sources (statistics, aggregators etc.),
 * or acquired from physical sensors - then they should implement the Sensor interface.
 */
public interface Source<T> extends Observable {

    T waitForValue();

    /**
     * Asynchronous interface.
     */
    void queryValue();
    Optional<T> getValue();

}
