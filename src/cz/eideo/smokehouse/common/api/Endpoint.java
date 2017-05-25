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
     * Instructs the API to send value over.
     *
     * @param node the API node
     */
    void sendValue(Node node) throws IOException;

    /**
     * Attach new node.
     *
     * @param n
     */
    void addNode(Node<?> n);

    /**
     * Assign new key.
     * @return unique key
     */
    int assignKey();

}
