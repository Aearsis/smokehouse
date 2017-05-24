package cz.eideo.smokehouse.common.sensor;

/**
 * Recipes how to create sensors.
 */
public interface SensorFactory {

    Thermometer createThermometer(String name) throws FactoryException;

    ThermoArea createThermoArea(String name) throws FactoryException;

    class FactoryException extends Exception {
        FactoryException(String s, Throwable throwable) {
            super(s, throwable);
        }
    }
}
