package cz.eideo.smokehouse.common.event;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The reference dispatcher implementation.
 *
 * All the features of the event loop are implemented here, either in the Dispatcher or the EventImpl.
 */
public class Dispatcher implements Runnable, EventFactory {

    private final static Logger logger = Logger.getLogger(Dispatcher.class.getName());

    private final Queue<EventImpl> eventQueue = new ArrayDeque<>();
    private final PriorityQueue<EventImpl> delayedEvents = new PriorityQueue<>(1, Comparator.comparing(a -> a.scheduledAt));
    private volatile boolean stopped = false;

    /**
     * The Event implementation this dispatcher uses.
     */
    private class EventImpl implements Event {

        private boolean scheduled = false;
        private Instant scheduledAt = null;
        private long scheduleRate = 0;
        private final Runnable callback;

        private EventImpl(Runnable callback) {
            this.callback = callback;
        }

        @Override
        public void schedule() {
            scheduleTo(null);
        }

        @Override
        public void scheduleWithDelay(long millis) {
            scheduleTo(Instant.now().plusMillis(millis));
        }

        @Override
        public void scheduleTo(Instant at) {
            scheduledAt = at;
            scheduleEvent(this);
        }

        @Override
        public void setRate(long rateMs) {
            this.scheduleRate = rateMs;
        }

        private synchronized void fire() {
            callback.run();
        }


        /**
         * Returns true, of the event should be fired just now.
         * @return whether we should fire the event
         */
        private boolean shallFire() {
            return scheduledAt == null || !scheduledAt.isAfter(Instant.now());
        }
    }

    @Override
    public Event createEvent(Runnable callback) {
        return new EventImpl(callback);
    }

    /**
     * Insert the event into the queue.
     */
    private synchronized void scheduleEvent(EventImpl event) {
        if (!event.scheduled) {
            event.scheduled = true;
            eventQueue.add(event);
            notify();
        }
    }

    /**
     * The event-loop implementation.
     * Unless explicitly stopped, never returns.
     */
    public void run() {
        try {
            while (!stopped) {
                EventImpl event = getNextEvent();
                event.scheduled = false;

                // We will need the Instant to accurately reschedule the event
                if (event.scheduleRate > 0 && event.scheduledAt == null)
                    event.scheduledAt = Instant.now();

                event.fire();

                // Reschedule the event to the correct time
                if (event.scheduleRate > 0)
                    event.scheduleTo(event.scheduledAt.plusMillis(event.scheduleRate));
            }
        } catch (InterruptedException ignored) {
            logger.log(Level.WARNING, "The event dispatcher was ended by interrupt.");
        }
    }

    /**
     * Return next event to be fired.
     *
     * @return event that should be fired
     * @throws InterruptedException when the waiting was interrupted
     */
    private synchronized EventImpl getNextEvent() throws InterruptedException {
        do {
            if (!delayedEvents.isEmpty()) {
                final EventImpl firstDelayedEvent = delayedEvents.peek();
                if (firstDelayedEvent.shallFire()) {
                    return delayedEvents.remove();
                }

                if (eventQueue.isEmpty()) {
                    long timeout = Duration.between(Instant.now(), firstDelayedEvent.scheduledAt).toMillis();
                    if (timeout > 0) {
                        wait(timeout);
                    }
                    continue;
                }
            }

            if (eventQueue.isEmpty()) {
                wait();
            } else {
                // Check the first ASAP event, either fire or delay it
                final EventImpl nextEvent = eventQueue.remove();
                if (nextEvent.shallFire()) {
                    return nextEvent;
                } else {
                    delayedEvents.add(nextEvent);
                }
            }
        } while (true);
    }


    /**
     * Stop the loop processing.
     */
    public void stop() {
        stopped = true;
    }
}
