package cz.eideo.smokehouse.common.setup;

import cz.eideo.smokehouse.common.Setup;
import cz.eideo.smokehouse.common.RealNumberObserver;
import cz.eideo.smokehouse.common.ThermoArea;
import cz.eideo.smokehouse.common.sensor.Thermometer;
import cz.eideo.smokehouse.showcase.*;

/**
 * Simple testing setup containing thermometers with random feeders.
 */
public class Testing extends Setup {

    private final static int DELAY = 1000;

    private Thermometer[] thermometers;
    private ThermoArea[] areas;
    private RealNumberRandomFeeder[] feeders;

    @Override
    public void setupCommon() {
        thermometers = new Thermometer[18];
        for (int i = 0; i < 18; i++)
            thermometers[i] = new Thermometer();

        areas = new ThermoArea[2];
        for (int i = 0; i < 2; i++) {
            ThermoArea a = new ThermoArea();
            for (int j = i * 9; j < 9 + i * 9; i++)
                a.addThermometer(thermometers[i]);
            areas[i] = a;
        }
    }

    @Override
    public void setupServer() {
       feeders = new RealNumberRandomFeeder[18];

        for (int i = 0; i < 18; i++) {
            final String format = String.format("Temperature on %d changed to %%.1f °C", i);
            thermometers[i].attachObserver(new RealNumberObserver() {
                public void signal() { System.err.println(String.format(format, sensor.getValue())); } });
            feeders[i] = new RealNumberRandomFeeder(createSimulatingGenerator(), DELAY);
            feeders[i].setSensor(thermometers[i]);
        }
    }

    /**
     * This pipeline from generators simulate temperatures that behave similarly to real temperatures.
     */
    private RealNumberGenerator createSimulatingGenerator() {
        RealNumberGenerator gen = () -> 0;                     // Second derivation is to be expected zero
        gen = new GaussianGenerator(0.4, gen);          //                      .. with sigma = 0.4
        gen = new IntegratingGenerator(0, gen);
        gen = new ClampingGenerator(-0.002 * DELAY, 0.002 * DELAY, gen);    // max 3 °C/sec
        gen = new IntegratingGenerator(80, gen);
        gen = new ClampingGenerator(22, 120, gen);   // Clamp temperature to real values
        gen = new GaussianGenerator(0.05, gen);           // .. but add a random offset to make it look better on extremes
        return gen;
    }

    @Override
    public void startServer() {
        for (int i = 0; i < 18; i++)
            feeders[i].start();
    }

    @Override
    public void stopServer() {
        for (int i = 0; i < 18; i++)
            feeders[i].stop();
    }
}
