package cz.eideo.smokehouse.common.api;

/**
 * One side of the API. Implementations should either fetch values from the other side, or provide them.
 */
public interface Endpoint {

    /**
     * Instructs the API to fetch value from node.
     *
     * @param key the API key
     * @return true if actually announced query (or just plain call)
     */
    default boolean announceQueryValue(int key) {
        return false;
    }

    /**
     * Instructs the API to send value over.
     *
     * @param key the API key
     * @return true if actually transmitted the value
     */
    default boolean announceValueChanged(int key) {
        return false;
    }

    /**
     * Attach new node. Should also assign a key to the node.
     *
     * @param n
     */
    int addNode(Node n);

}
