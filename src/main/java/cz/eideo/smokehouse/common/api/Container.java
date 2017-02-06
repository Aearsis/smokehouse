package cz.eideo.smokehouse.common.api;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Basic endpoint which at least remembers the nodes.
 */
abstract public class Container implements Iterable<Node>, Endpoint {

    protected ArrayList<Node> nodes = new ArrayList<>();

    public int addNode(Node n) {
        int key = nodes.size();
        nodes.add(n);
        return key;
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public void dumpNodes() {
        for (int i = 0; i < nodes.size(); i++) {
            System.err.printf("node %d: ", i);
            nodes.get(i).dump();
        }
    }
}
