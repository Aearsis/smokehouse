package cz.eideo.smokehouse.common;

/**
 * Setup represents the physical setup of the sensors.
 * It is fixed for a session, and whenever the physical layout
 * of the sensors changes, it is necessary to create a new setup.
 * However, adding new statistics should be safe.
 *
 * Setup is identified by its classname.
 */
abstract public class Setup {

    protected Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Initialize all sources locally, attach them to API.
     */
    public void setupSensors() {}

}
