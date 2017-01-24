package cz.eideo.smokehouse.common.setup;

import cz.eideo.smokehouse.common.Setup;
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
 */
public class CubeSetup extends Setup {

    private Thermometer[] thermometers;

    @Override
    public void setupSensors() {
        thermometers = new Thermometer[18];
        for (int i = 0; i < 18; i++) {
            Thermometer t = new Thermometer();
            t.attachAPI(session.getAPI());
            thermometers[i] = t;
        }
    }

    public Thermometer getThermometer(int i)
    {
        return thermometers[i];
    }


}
