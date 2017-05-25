package cz.eideo.smokehouse.common.event;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dispatcher implements Runnable, EventFactory {

    Queue<EventImpl> eventQueue = new ArrayDeque<>();
    PriorityQueue<EventImpl> delayedEvents = new PriorityQueue<>(1, Comparator.comparing(a -> a.scheduledAt));

    volatile boolean stopped = false;

    private final static Logger logger = Logger.getLogger(Dispatcher.class.getName());

    private class EventImpl implements Event {

        boolean scheduled = false;
        Instant scheduledAt = null;
        long scheduleRate = 0;

        final Runnable callback;

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

        synchronized void fire() {
            callback.run();
        }

        boolean shallFire() {
            return scheduledAt == null || !scheduledAt.isAfter(Instant.now());
        }
    }

    @Override
    public Event createEvent(Runnable callback) {
        return new EventImpl(callback);
    }

    private synchronized void scheduleEvent(EventImpl event) {
        if (!event.scheduled) {
            event.scheduled = true;
            eventQueue.add(event);
            notify();
        }
    }

    @Override
    public void run() {
        try {
            while (!stopped) {
                EventImpl event = getNextEvent();
                event.scheduled = false;

                // We will need the value to reschedule the event
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
     * @throws InterruptedException
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


    public void stop() {
        stopped = true;
    }
    }
