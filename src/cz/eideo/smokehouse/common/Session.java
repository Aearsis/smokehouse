package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.Endpoint;

/**
 * Sessions represent one smoking. They have fixed setup, which is saved into the storage and loaded.
 */
public class Session {

    public enum State {
        NEW, INITIALIZED
    }

    private final Endpoint endpoint;
    private final SessionStorage storage;

    private Setup setup;

    public Session(SessionStorage storage, Endpoint api) {
        this.storage = storage;
        this.endpoint = api;
    }

    /**
     * Create an instance of setup, according to the setupClass.
     */
    public void loadSetup() {
        if (setup != null)
            throw new IllegalStateException("Setup already set or loaded.");

        try {
            setup = (Setup) storage.getSetupClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(String.format("Setup %s is not constructible.", storage.getSetupClass()), e);
        }
    }

    public Setup getSetup() {
        return setup;
    }

    public SessionStorage getStorage() {
        return storage;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

}
