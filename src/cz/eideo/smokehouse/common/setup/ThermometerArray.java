package cz.eideo.smokehouse.common.setup;

import cz.eideo.smokehouse.common.sensor.Thermometer;

/**
 * Created by ohlavaty on 5/24/17.
 */
public interface ThermometerArray {
    Thermometer getThermometer(int i);
}
