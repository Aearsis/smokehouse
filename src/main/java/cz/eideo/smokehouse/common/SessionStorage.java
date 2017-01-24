package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.Session.State;
import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.StorageNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Storage for sessions, by default empty, relying on API provided values.
 */
public class SessionStorage {

    private StorageNode<Class> setupClass = new StorageNode<Class>() {
        @Override
        public void writeValue(DataOutputStream stream) throws IOException {
            stream.writeUTF(getValue().getCanonicalName());
        }

        @Override
        public void readValue(DataInputStream dataStream) throws IOException {
            String canonicalName = dataStream.readUTF();
            try {
                setValue(Class.forName(canonicalName));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    };

    public void setSetupClass(Class setupClass) {
        this.setupClass.setValue(setupClass);
    }

    public Class getSetupClass() {
        return setupClass.getValue();
    }

    private StorageNode<State> state = new StorageNode<State>() {
        @Override
        public void writeValue(DataOutputStream stream) throws IOException {
            stream.writeUTF(value.toString());
        }

        @Override
        public void readValue(DataInputStream dataStream) throws IOException {
            value = State.valueOf(dataStream.readUTF());
        }
    };

    public State getState(){
        return state.getValue();
    }

    public void setState(State state) {
        this.state.setValue(state);
    }

    public void attachAPI(Endpoint API)
    {
        setupClass.attachAPI(API);
        state.attachAPI(API);
    }

    /**
     * Flush into permanent storage. Called after a batch of changes.
     */
    public void flush() { }
}
