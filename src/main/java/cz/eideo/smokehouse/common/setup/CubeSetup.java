package cz.eideo.smokehouse.common.setup;

import cz.eideo.smokehouse.common.Setup;
import cz.eideo.smokehouse.common.sensor.ThermoArea;
import cz.eideo.smokehouse.common.sensor.Thermometer;

/**
 * This setup contains 18 thermometers in two layers.
 * Each layer is in form of 3x3 even grid of thermometers.
 * Their numbers are fixed this way:
 *
 *           11   14   17
 *         10   13   16
 *        9   12   15
 *
 *            2    5    8
 *          1    4    7
 *        0    3    6
 *
 * Every column has a ThermoArea, numbered as the lower sensor.
 * Planes has thermoareas (0 bottom, 1 up) and the whole cube has an area.
 */
public class CubeSetup extends Setup {

    private Thermometer[] thermometers;
    private ThermoArea[] thermoAreas;

    private void createThermometer(int i) {
        Thermometer t = new Thermometer(session.getAPI());
        thermometers[i] = t;
        getColumnArea(i % 9).addThermometer(t);
        getPlaneArea(i >= 9 ? 1 : 0).addThermometer(t);
        getCubeArea().addThermometer(t);
    }

    @Override
    public void setupSensors() {
        thermometers = new Thermometer[18];
        thermoAreas = new ThermoArea[1 + 2 + 9];

        for (int i = 0; i < thermoAreas.length; i++)
            thermoAreas[i] = new ThermoArea(session.getAPI());

        for (int i = 0; i < 18; i++)
           createThermometer(i);
    }

    public Thermometer getThermometer(int i) {
        return thermometers[i];
    }

    public ThermoArea getColumnArea(int i) {
        return thermoAreas[3 + i];
    }

    public ThermoArea getPlaneArea(int i) {
        return thermoAreas[1 + i];
    }

    public ThermoArea getCubeArea() {
        return thermoAreas[0];
    }
}
