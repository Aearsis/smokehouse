/**
 * The API is basically a shared key-value middleware.
 * <p>
 * First, an instance of an {@link cz.eideo.smokehouse.common.api.Endpoint} is created.
 * The endpoint is the communicator sending and receiving network packets.
 * <p>
 * Next, for every value we want to share a {@link cz.eideo.smokehouse.common.api.Node}
 * is created. The Node offers an asynchronous access to the value, which is used by the API
 * to send and answer Queries, and to send updates.
 * <p>
 * There are two implementations of a Node - {@link cz.eideo.smokehouse.common.api.RemoteNode}
 * and {@link cz.eideo.smokehouse.common.api.LocalNode}. The LocalNode is the owner of a value,
 * which is being broadcasted to RemoteNodes running elsewhere.
 * <p>
 * The configurability is achieved by passing a {@link cz.eideo.smokehouse.common.api.NodeFactory}
 * to the units, which then create all the nodes they need.
 * <p>
 * An important property of the network protocol is, that it's not self-describing. In order to
 * spare network bandwidth (targeting mobile platforms), nodes are required to be created in the
 * same in every application. The Setup is then what defines the order, hence the mapping between
 * API keys to nodes.
 * <p>
 * All values are then sent over the network as a (key, encoded(velue)) pairs, taking just 4B
 * per thermal value shared.
 */
package cz.eideo.smokehouse.common.api;