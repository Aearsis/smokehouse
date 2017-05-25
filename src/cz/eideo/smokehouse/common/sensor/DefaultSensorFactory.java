package cz.eideo.smokehouse.common.sensor;

import cz.eideo.smokehouse.common.api.NodeFactory;
import cz.eideo.smokehouse.common.event.EventFactory;

public class DefaultSensorFactory implements SensorFactory {

    final NodeFactory nodeFactory;
    final EventFactory eventFactory;

    public DefaultSensorFactory(NodeFactory nodeFactory, EventFactory eventFactory) {
        this.nodeFactory = nodeFactory;
        this.eventFactory = eventFactory;
    }

    @Override
    public Thermometer createThermometer(String name) throws FactoryException {
        return new Thermometer(nodeFactory.getNamespace(name), eventFactory);
    }

    @Override
    public ThermoArea createThermoArea(String name){
        return new ThermoArea(nodeFactory.getNamespace(name), eventFactory);
    }
}
