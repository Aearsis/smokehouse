package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.api.Endpoint;

public class DefaultSensorFactory implements SensorFactory {

    private final Endpoint API;

    public DefaultSensorFactory(Endpoint api) {
        API = api;
    }

    @Override
    public Thermometer createThermometer(String name) throws FactoryException {
        return new Thermometer(API);
    }

    @Override
    public ThermoArea createThermoArea(String name) {
        return new ThermoArea(API);
    }
}
