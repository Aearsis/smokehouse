package cz.eideo.smokehouse.common.setup;

import cz.eideo.smokehouse.common.Setup;
import cz.eideo.smokehouse.common.sensor.SensorFactory;
import cz.eideo.smokehouse.common.sensor.ThermoArea;
import cz.eideo.smokehouse.common.sensor.Thermometer;

/**
 * This setup contains 18 thermometers in two layers.
 * Each layer is in form of 3x3 even grid of thermometers.
 * Their numbers are fixed this way:
 * <p>
 * 11   14   17
 * 10   13   16
 * 9   12   15
 * <p>
 * 2    5    8
 * 1    4    7
 * 0    3    6
 * <p>
 * Every column has a ThermoArea, numbered as the lower sensor.
 * Planes has thermoareas (0 bottom, 1 up) and the whole cube has an area.
 */
public class CubeSetup extends Setup implements ThermometerArray {

    private Thermometer[] thermometers;
    private ThermoArea[] thermoAreas;

    private void createThermometer(int i) {
        try {
            Thermometer t = sensorFactory.createThermometer(Integer.toHexString(i));
            thermometers[i] = t;
            getColumnArea(i % 9).addThermometer(t);
            getPlaneArea(i >= 9 ? 1 : 0).addThermometer(t);
            getCubeArea().addThermometer(t);
        } catch (SensorFactory.FactoryException e) {
            throw new RuntimeException("Cannot create Thermometer.", e);
        }
    }

    @Override
    public void setupSensors() {
        if (sensorFactory == null)
            throw new Error("SensorFactory must be set first.");

        thermometers = new Thermometer[18];
        thermoAreas = new ThermoArea[1 + 2 + 9];

        for (int i = 0; i < thermoAreas.length; i++)
            try {
                thermoAreas[i] = sensorFactory.createThermoArea(Integer.toHexString(i));
            } catch (SensorFactory.FactoryException e) {
                throw new RuntimeException("Cannot create ThermoArea.", e);
            }

        for (int i = 0; i < 18; i++)
            createThermometer(i);
    }

    @Override
    public Thermometer getThermometer(int i) {
        return thermometers[i];
    }

    public ThermoArea getCubeArea() {
        return thermoAreas[0];
    }

    public ThermoArea getPlaneArea(int i) {
        assert i > 0 && i < 2;
        return thermoAreas[1 + i];
    }

    public ThermoArea getColumnArea(int i) {
        assert i > 0 && i < 9;
        return thermoAreas[3 + i];
    }
}

