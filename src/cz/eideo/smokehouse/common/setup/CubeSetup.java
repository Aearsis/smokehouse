package cz.eideo.smokehouse.common.setup;

import cz.eideo.smokehouse.common.Setup;
import cz.eideo.smokehouse.common.sensor.SensorFactory;
import cz.eideo.smokehouse.common.sensor.ThermoArea;
import cz.eideo.smokehouse.common.sensor.Thermometer;

import java.util.HashMap;

/**
 * This setup contains 18 thermometers in two layers.
 * Each layer is in form of 3x3 even grid of thermometers.
 * Their numbers are fixed this way:
 * <pre>
 *      0   1   2
 *      3   4   5
 *      6   7   8
 *
 *      9   10  11
 *      12  13  14
 *      15  16  17
 * </pre>
 * Every column has a ThermoArea, numbered as the upper sensor.
 * Planes has thermoareas (0 up, 1 bottom) and the whole cube has an area.
 */
public class CubeSetup extends Setup {

    private Thermometer[] thermometers;
    private ThermoArea[] thermoAreas;

    private final static HashMap<Integer, String> columnNames = new HashMap<Integer, String>() {{
        put(0, "back-left");    put(1, "back-middle");  put(2, "back-right");
        put(3, "middle-left");  put(4, "middle");       put(5, "middle-right");
        put(6, "front-left");   put(7, "front-middle"); put(8, "front-right");
    }};

    private void createThermometer(int i) {
        try {
            final String name = (i >= 9 ? "bottom-" : "top-") + columnNames.get(i % 9);
            final Thermometer t = thermometers[i] = sensorFactory.createThermometer(name);
            getColumnArea(i % 9).addThermometer(t);
            getPlaneArea(i / 9).addThermometer(t);
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

