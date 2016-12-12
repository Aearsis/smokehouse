package cz.eideo.smokehouse.common;

/**
 * Storage requirements for sessions.
 */
abstract public class SessionStorage {

    protected Session.State state = Session.State.NEW;
    protected Class setupClass = Setup.getCurrentSetup();

    public Class getSetupClass() {
        return setupClass;
    }

    public void setSetupClass(Class setupClass) {
        this.setupClass = setupClass;
    }

    public Session.State getState() {
        return state;
    }

    public void setState(Session.State state) {
        this.state = state;
    }
}
