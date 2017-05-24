package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.Session.State;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.StorageNode;
import cz.eideo.smokehouse.common.api.codec.ClassCodec;
import cz.eideo.smokehouse.common.api.codec.Codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Storage for sessions, by default empty, relying on API provided values.
 */
public class SessionStorage {

    private final StorageNode<Class> setupClass;

    public SessionStorage(Endpoint API) {
        setupClass = new StorageNode<>(API, new ClassCodec(), "setup class");
        state = new StorageNode<>(API, new Codec<State>() {
            @Override
            public void encode(State value, DataOutputStream stream) throws IOException {
                stream.writeUTF(value.toString());
            }

            @Override
            public State decode(DataInputStream stream) throws IOException {
                return State.valueOf(stream.readUTF());
            }
        }, "session state");
    }

    public void setSetupClass(Class setupClass) {
        this.setupClass.setValue(setupClass);
    }

    public Class getSetupClass() {
        return setupClass.getValue();
    }

    private final StorageNode<State> state;

    public State getState() {
        return state.getValue();
    }

    public void setState(State state) {
        this.state.setValue(state);
    }

    /**
     * Flush into permanent storage. Called after a batch of changes.
     */
    public void flush() {
    }

}
