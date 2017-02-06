package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.BlackHole;
import cz.eideo.smokehouse.common.sensor.Thermometer;
import org.junit.Test;

import static org.junit.Assert.*;

public class ThermometerTest {

    @Test
    public void testValue() throws Exception {
        Thermometer t = new Thermometer(BlackHole.SINGULARITY);
        t.setValue(42d);
        assertTrue(t.getValue() == 42);
    }
}