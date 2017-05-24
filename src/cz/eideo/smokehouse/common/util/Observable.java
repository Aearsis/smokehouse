package cz.eideo.smokehouse.common.util;

/**
 * Subjects being monitored by Observers.
 */
public interface Observable {

    void attachObserver(Observer monitor);

}
