package cz.eideo.smokehouse.common.event;

/**
 * Subjects being monitored by Observer Events.
 *
 * Whenever the observee notifies, the event will be scheduled.
 */
public interface EventObservable {

    void attachObserver(Event signalEvent);

}
