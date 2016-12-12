package cz.eideo.smokehouse.showcase;

import cz.eideo.smokehouse.common.RealNumberSensor;
import org.junit.Test;

import static org.junit.Assert.*;

public class RealNumberRandomFeederTest {

    static class TestSensor extends RealNumberSensor {
        public int counter = 0;
        @Override
        public void setValue(double value) {
            counter++;
            super.setValue(value);
        }
    }

    @Test
    public void testFeeder() throws Exception {
        RealNumberRandomFeeder f = new RealNumberRandomFeeder(() -> 42, 0);
        TestSensor s = new TestSensor();

        f.setSensor(s);
        f.start();
        while (s.counter < 2) Thread.yield();
        f.stop();

        assertTrue(s.getValue() == 42);
    }
}