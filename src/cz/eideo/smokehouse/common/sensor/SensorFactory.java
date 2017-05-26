package cz.eideo.smokehouse.common.sensor;

/**
 * A Factory for all known types of real sensors.
 *
 * Enables the Setup to focus on how the sensors are structured, not how are they configured.
 */
public interface SensorFactory {

    /**
     * Create a Thermometer.
     * @param name An arbitrary name. Can be used as a storage identifier.
     * @return new Thermometer
     * @throws FactoryException when it is not possible to create the sensor
     */
    Thermometer createThermometer(String name) throws FactoryException;

    /**
     * Create a ThermoArea.
     * @param name An arbitrary name. Can be used as a storage identifier.
     * @return new ThermoArea
     * @throws FactoryException when it is not possible to create the sensor
     */
    ThermoArea createThermoArea(String name) throws FactoryException;

    class FactoryException extends Exception {
        FactoryException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }
}
