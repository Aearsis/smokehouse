package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.BlackHole;
import cz.eideo.smokehouse.common.sensor.ThermoArea;
import cz.eideo.smokehouse.common.sensor.Thermometer;
import cz.eideo.smokehouse.common.util.Observer;
import org.junit.Test;

import static org.junit.Assert.*;

public class ThermoAreaTest {

    private class TestObserver implements Observer {
        boolean signalled = false;

        public void signal() {
            signalled = true;
        }
    }

    @Test
    public void testValue() throws Exception {
        ThermoArea ta = new ThermoArea(BlackHole.SINGULARITY);
        TestObserver to = new TestObserver();
        ta.attachObserver(to);

        Thermometer t = new Thermometer(BlackHole.SINGULARITY);
        ta.addThermometer(t);

        t.setValue(42d);
        assertTrue(to.signalled);
    }

}