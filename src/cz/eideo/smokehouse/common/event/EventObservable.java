package cz.eideo.smokehouse.common.event;

/**
 * Subjects being monitored by Observer events.
 */
public interface EventObservable {

    void attachObserver(Event signalEvent);

}
