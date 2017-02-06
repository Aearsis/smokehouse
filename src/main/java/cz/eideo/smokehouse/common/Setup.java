package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.sensor.SensorFactory;

/**
 * Setup represents the physical setup of the sensors.
 * It is fixed for a session, and whenever the physical layout
 * of the sensors changes, it is necessary to create a new setup,
 * otherwise loading old sessions will become impossible.
 *
 * However, adding new statistics to sensors themselves should be safe.
 *
 * Setup is identified by its classname.
 */
abstract public class Setup {

    protected SensorFactory sensorFactory;

    /**
     * It is mandatory to set a sensorFactory before calling setupSensors.
     * @param factory
     */
    public void setSensorFactory(SensorFactory factory){
        sensorFactory = factory;
    }

    /**
     * Initialize all sources locally, attach them to API.
     */
    public void setupSensors() {}
}
