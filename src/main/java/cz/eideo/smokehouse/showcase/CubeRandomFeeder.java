package cz.eideo.smokehouse.showcase;

import cz.eideo.smokehouse.common.RealNumberObserver;
import cz.eideo.smokehouse.common.sensor.Thermometer;
import cz.eideo.smokehouse.common.setup.CubeSetup;
import cz.eideo.smokehouse.showcase.*;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CubeRandomFeeder {

    private static final long DELAY = 1000;
    private CubeSetup setup;
    private RealNumberRandomFeeder[] feeders;

    private RealNumberGenerator shared;
    private RealNumberGenerator initial;


    public CubeRandomFeeder(CubeSetup setup)
    {
        this.setup = setup;
        shared = createSharedGenerator();
        initial = new GaussianGenerator(5, () -> 80);
    }

    private RealNumberGenerator createSharedGenerator() {
        RealNumberGenerator gen = () -> 0;                      // Second derivation is to be expected zero
        gen = new GaussianGenerator(0.01, gen);            //                      .. with sigma = 0.4
        gen = new ClampingGenerator(-0.1, 0.1, gen);   // max 0.1 °C/sec^2
        gen = new IntegratingGenerator(0, gen);
        gen = new ClampingGenerator(-1, 1, gen);    // max 1 °C/sec
        return gen;
    }

    /**
     * This pipeline from generators simulate temperatures that behave similarly to real temperatures.
     */
    private RealNumberGenerator createSimulatingGenerator() {
        RealNumberGenerator gen = new IntegratingGenerator(initial.generateNext(), this.shared);
        gen = new ClampingGenerator(22, 120, gen);   // Clamp temperature to real values
        gen = new GaussianGenerator(0.05, gen);           // .. but add a random offset to make it look better on extremes
        return gen;
    }

    public void setup() {
        feeders = new RealNumberRandomFeeder[18];
        Random r = new Random();

        for (int i = 0; i < 18; i++) {
            final String format = String.format("Temperature on %d changed to %%.1f °C", i);

            Thermometer t = setup.getThermometer(i);
            feeders[i] = new RealNumberRandomFeeder(createSimulatingGenerator());
            feeders[i].setSensor(t);
        }
    }

    ScheduledExecutorService feederExecutor;

    public void start() {
        feederExecutor = Executors.newSingleThreadScheduledExecutor();

        for (int i = 0; i < 18; i++) {
            int index = i;
            feederExecutor.scheduleAtFixedRate(feeders[index]::emit, 0, DELAY, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        feederExecutor.shutdown();
    }
}
