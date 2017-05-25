package cz.eideo.smokehouse.common.event;

public interface EventFactory {
    Event createEvent(Runnable callback);
}
