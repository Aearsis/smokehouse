package cz.eideo.smokehouse.showcase;

import cz.eideo.smokehouse.common.feeder.RealNumberFeeder;

import java.security.InvalidParameterException;

/**
 * Feeds the sensor with random values.
 */
public class RealNumberRandomFeeder extends RealNumberFeeder {

    /**
     * Delay between emits.
     */
    private long delay;

    private RealNumberGenerator generator;
    private Worker worker;

    public RealNumberRandomFeeder(RealNumberGenerator generator, long delay) {
        this.generator = generator;
        this.delay = delay;
    }

    private class Worker extends Thread {
        boolean run = true;

        @Override
        public void run() {
            do {
                double value = generator.generateNext();
                sensor.setValue(value);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) { }
            } while (run);
        }
    }

    public void start() {
        if (sensor == null)
            throw new InvalidParameterException("No sensor set yet.");

        if (worker != null)
            throw new InvalidParameterException("Worker already running.");

        worker = new Worker();
        worker.start();
    }

    public void stop() {
        if (worker == null)
            throw new InvalidParameterException("Worker not started!");

        worker.run = false;
        while (true) try {
                worker.join();
                break;
            } catch (InterruptedException ignored) { }
    }

}
