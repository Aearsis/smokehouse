package cz.eideo.smokehouse.common.event;

@FunctionalInterface
public interface EventFactory {

    /**
     * Create an event, firing of which will run a callback.
     *
     * @param callback the callback to be run after the event fires
     * @return a newly created Event
     */
    Event createEvent(Runnable callback);

}
