package cz.eideo.smokehouse.common.api;

import java.io.IOException;

/**
 * One endpoint of the API.
 *
 * API is composed of "nodes". The API implements a remotely-accessible hashtable,
 * which operates asynchronously.
 *
 * The values can be queried and announced.
 */
public interface Endpoint {

    /**
     * Instructs the API to fetch value from node.
     *
     * @param node the API node
     */
    void sendQuery(Node node);

    /**
     * When you block waiting for a value to be received,
     * you must flush the queue inside your event handler.
     * In other cases, it is flushed automatically through
     * a scheduled event.
     */
    void flushQueue();

    /**
     * Instructs the API to send value over.
     *
     * @param node the API node
     * @throws IOException when there was an error writing the value
     */
    void sendValue(Node node) throws IOException;

    /**
     * Attach new node to the API. The node must already have a key assigned.
     */
    void addNode(Node<?> n);

    /**
     * Assign new key.
     * @return unique key
     */
    int assignKey();

}
