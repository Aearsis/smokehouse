package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.setup.Testing;

/**
 * Setup represents the physical setupCommon of the sensors.
 * It is fixed for a session, and whenever the physical layout
 * of the sensors changes, it is necessary to create a new setupCommon.
 *
 * However, adding new statistics should be safe.
 *
 * Setup is identified by its classname.
 */
abstract public class Setup {

    private Session session;

    /**
     * Rebuilding a program is less work than changing the physical setup,
     * so let's avoid having to specify setupCommon class every start.
     *
     * This field will be used as a default when creating new session.
     * When the physical setup changes, this program can be rebuilt
     * while still able to load older sessions.
     */
    public static Class getCurrentSetup() {
        return Testing.class;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setupCommon() {}
    public void setupServer() {}

    public void startServer() {}

    public void stopServer() {}
}
