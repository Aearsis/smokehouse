/**
 * This package contains the event loop and dispatcher implementation.
 * <p>
 * First, the application have to create an {@link cz.eideo.smokehouse.common.event.Dispatcher} instance.
 * Then, the event dispatcher serves as an {@linkplain cz.eideo.smokehouse.common.event.Event event} factory.
 * Events have to be created in the "configuration" time, saving memory allocations later.
 * Also, the API for them is then very simple - just call {@link cz.eideo.smokehouse.common.event.Event#schedule()}
 * and it will happen.
 * <p>
 * The features then include scheduling with delay, to a fixed instant, or with fixed rate.
 */
package cz.eideo.smokehouse.common.event;