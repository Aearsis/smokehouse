package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.api.NodeFactory;

public class DefaultSensorFactory implements SensorFactory {

    private NodeFactory factory;

    public DefaultSensorFactory(NodeFactory factory) {
        this.factory = factory;
    }

    @Override
    public Thermometer createThermometer(String name) throws FactoryException {
        return new Thermometer(factory.getNamespace(name));
    }

    @Override
    public ThermoArea createThermoArea(String name){
        return new ThermoArea(factory.getNamespace(name));
    }
}
