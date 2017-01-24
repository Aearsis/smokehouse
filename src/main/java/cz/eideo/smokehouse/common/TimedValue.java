package cz.eideo.smokehouse.common;

import java.time.Instant;

/**
 * Value of a sensor in time.
 */
public class TimedValue<T> {

    final T value;
    final Instant time;

    public TimedValue(T value, Instant time) {
        this.value = value;
        this.time = time;
    }

    public T getValue() {
        return value;
    }

    public Instant getInstant() {
        return time;
    }
}
