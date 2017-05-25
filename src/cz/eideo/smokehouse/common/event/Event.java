package cz.eideo.smokehouse.common.event;

import java.time.Instant;

/**
 * An event that can be scheduled.
 *
 * Events scheduled ASAP will be fired earlier than any events scheduled later.
 * Events scheduled with delay / to absolute time may be postponed to satisfy the delay,
 * thus be fired later than later-scheduled events.
 */
public interface Event {

    /**
     * Schedule this event to run ASAP.
     */
    void schedule();

    /**
     * Schedule this Event to run anytime after a delay.
     *
     * @param millis milliseconds to wait (at least) before firing
     */
    void scheduleWithDelay(long millis);

    /**
     * Schedule this Event to fire anytime after an absolute time
     * @param at the least time this event will fire
     */
    void scheduleTo(Instant at);

    /**
     * After firing, this event will be scheduled again.
     *
     * Though it cannot be guaranteed, that this event will be fired with exactly the rate,
     * it will be fired the same number of times as it would have been accurate.
     *
     * @param rateMs delay between fires in milliseconds. 0 to disable.
     */
    void setRate(long rateMs);
}
