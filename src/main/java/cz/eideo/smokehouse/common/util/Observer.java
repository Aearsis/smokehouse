package cz.eideo.smokehouse.common.util;

/**
 * Base for all observers.
 */
public interface Observer {

    /**
     * The monitor was attached to a sensor.
     */
    default void attached(Observable s) {}

    /**
     * The monitorable signalled value changed.
     */
    void signal();
}
