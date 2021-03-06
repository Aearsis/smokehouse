package cz.eideo.smokehouse.common.api;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Basic endpoint which at least remembers the nodes.
 */
abstract public class Container implements Iterable<Node<?>>, Endpoint {

    final HashMap<Integer, Node<?>> nodes = new HashMap<>();

    public void addNode(Node n) {
        nodes.put(n.getApiKey(), n);
    }

    @Override
    public Iterator<Node<?>> iterator(){
        return nodes.values().iterator();
    }

    @Override
    public int assignKey() {
        return nodes.size();
    }
}
