package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.util.Observable;

import java.time.Instant;

/**
 * Source is the basic API unit, having its API key and values in time.
 *
 * The value can be of any type, and can be either calculated from other sources (statistics, aggregators etc.),
 * or acquired from physical sensors - then they should implement the Sensor interface.
 */
public interface Source<T> extends Observable {

    /**
     * Aside from not knowing which one to ask,
     * source can be attached to multiple APIs.
     */
    default void attachAPI(Endpoint api) { }

    T getValue();
    T getTimedValue(Instant time);

}
