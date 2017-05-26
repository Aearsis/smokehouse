/**
 * <p>
 * This package contains Smokehouse "library", common for all programs that take part in
 * this IoT framework.
 * </p>
 * <p>
 * It consists of several subpackages, that are documented separately.
 * </p>
 * <p>
 * Everything turns around sensors and sources. These are just values of a given type,
 * which represents something in the real world. {@linkplain cz.eideo.smokehouse.common.sensor.Source Sources}
 * are just read-only values, {@linkplain cz.eideo.smokehouse.common.sensor.Sensor sensors} can be fed a value by Feeders.
 * </p>
 * <p>
 * The real arrangement of sensors is represented by the {@link cz.eideo.smokehouse.common.Setup},
 * which is just one so far. To provide backwards compatibility with recorded files, a change of a layout
 * should be reflected by implementing a different Setup, as the Setup class is recorded together with the data.
 * </p>
 * <p>
 * The values are held in so-called API {@linkplain cz.eideo.smokehouse.common.api.Node Nodes},
 * which opaquely share their value through {@link cz.eideo.smokehouse.common.api.Endpoint}.
 * Currently, the only Endpoint implementation is the {@link cz.eideo.smokehouse.common.api.MulticastEndpoint},
 * which multicasts values over the network. That way, we can run the same codebase, just configured differently,
 * and values will magically replicate from the server to all the clients. All of that without specifying any IP address :)
 * </p>
 * <p>
 * Everything internaly communicates using the observer pattern, as every change of value is announced.
 * But there is a notable difference - the handling does not take place immediately. Instead,
 * all handlers of Observer notifies are {@link cz.eideo.smokehouse.common.event.Event},
 * which serialize through an event loop in the {@link cz.eideo.smokehouse.common.event.Dispatcher}.
 * This is mainly because updates come in batches, and this approach recalculates (resends, stores etc.)
 * values only after all updates are done. The transformation from the usual observer pattern can be
 * looked upon as a change from DFS to BFS.
 * </p>
 * <p>
 * Last but not least, there are some statistic aggregators, which serve as another
 * {@linkplain cz.eideo.smokehouse.common.sensor.Source sources}.
 * </p>
 * <p>
 * There is also a support for storing the values recorded, but it is missing the viewing part.
 * </p>
 */
package cz.eideo.smokehouse.common;