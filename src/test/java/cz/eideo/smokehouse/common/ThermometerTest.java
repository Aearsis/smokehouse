package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.sensor.Thermometer;
import org.junit.Test;

import static org.junit.Assert.*;

public class ThermometerTest {

    @Test
    public void testValue() throws Exception {
        Thermometer t = new Thermometer();
        t.setValue(42);
        assertTrue(t.getValue() == 42);
    }
}