package cz.eideo.smokehouse.common;

/**
 * Sessions represent one smoking. They have fixed setup, which is saved into the storage and loaded.
 */
public class Session {

    public enum State {
        NEW, INITIALIZED
    }

    private SessionStorage storage;
    private Setup setup;

    public Session(SessionStorage storage) {
        this.storage = storage;
    }

    public void setSetup(Setup s) {
        if (setup != null)
            throw new IllegalStateException("Setup already set or loaded.");

        setup = s;
    }

    public void loadSetup() {
        if (setup != null)
            throw new IllegalStateException("Setup already set or loaded.");

        try {
            setup = (Setup) storage.getSetupClass().newInstance();
            setup.setSession(this);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(String.format("Setup %s nelze načítat dynamicky.", storage.getSetupClass()), e);
        }
    }

    public Setup getSetup() {
        return setup;
    }
}
