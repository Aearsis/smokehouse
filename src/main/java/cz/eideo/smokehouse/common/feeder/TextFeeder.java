package cz.eideo.smokehouse.common.feeder;

import cz.eideo.smokehouse.common.RealNumberFeeder;
import cz.eideo.smokehouse.common.RealNumberSource;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Parses input stream. Fixed number of sensors N. Reads double value in cyclic order.
 */
public class TextFeeder extends RealNumberFeeder {

    Scanner input;
    RealNumberSource[] sensors;

    int currentSensor = 0;

    public TextFeeder(InputStream input, RealNumberSource[] sensors) {
        this.input = new Scanner(input);
        this.sensors = sensors;
    }

    Thread worker = null;

    public void start() {
        if (worker != null)
            throw new IllegalStateException("Worker already running.");

        worker = new Thread() {
            @Override
            public void run() {
                while (!interrupted()) try {
                    readAndParseLine();
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        };
        worker.start();
    }

    public void stop() {
        if (worker == null)
            throw new IllegalStateException("Worker not running.");

        worker.interrupt();
        while (true) try {
            worker.join();
            break;
        } catch (InterruptedException ignored) { }
    }

    private void readAndParseLine() throws InterruptedException {
        double value = input.nextDouble();
        sensors[currentSensor].setValue(value);
        currentSensor = (currentSensor + 1) % sensors.length;
    }
}
