package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.Session.State;
import cz.eideo.smokehouse.common.api.Node;
import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.api.codec.ClassCodec;
import cz.eideo.smokehouse.common.api.codec.EnumCodec;

/**
 * Storage for sessions, by default empty, relying on API provided values.
 */
public class SessionStorage {

    private final Node<Class> setupClass;
    private final Node<State> state;

    public SessionStorage(NodeFactory nodeFactory) {
        setupClass = nodeFactory.create(ClassCodec.INSTANCE, "setup class");
        state = nodeFactory.create(new EnumCodec<>(State.class), "session state");
    }

    public void setSetupClass(Class setupClass) {
        this.setupClass.setValue(setupClass);
    }

    public Class getSetupClass() {
        return setupClass.waitForValue();
    }

    public State getState() {
        return state.waitForValue();
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
