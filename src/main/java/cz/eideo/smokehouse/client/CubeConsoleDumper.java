package cz.eideo.smokehouse.client;

import cz.eideo.smokehouse.common.sensor.Thermometer;
import cz.eideo.smokehouse.common.setup.CubeSetup;

import java.io.Console;
import java.io.PrintWriter;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This is a simple dumper of temperatures, made for the CubeSetup.
 */
public class CubeConsoleDumper {

    private static final long DELAY = 1000;
    private CubeSetup setup;

    private static final String zspace = "   ";
    private static final String format = "  %3.1f";

    /* TODO: Consider writing the sensor numbers explicitly */
    private static final int[] lineOrder = new int[] {11, 10, 9, -1,  2, 1, 0};


    public CubeConsoleDumper(CubeSetup setup) {
        this.setup = setup;
    }

    private void dump(Console c) {
        PrintWriter w = c.writer();
        w.print("\033[H\033[2J"); // Clear screen, home

        int z = 0;
        for (int line : lineOrder) {
            if (line == -1) {
                w.println();
                continue;
            }
            z = (z + 2) % 3;
            for (int i = 0; i < z; i++) w.print(zspace);

            for (int i = line; i < line + 9; i += 3) {
                Thermometer t = setup.getThermometer(i);
                w.printf(format, t.getValue());
            }
            w.println();
            w.println();
        }

        w.flush();
    }

    ScheduledExecutorService feederExecutor;

    public void start() {
        feederExecutor = Executors.newSingleThreadScheduledExecutor();

        final Console c = System.console();
        if (c != null)
            feederExecutor.scheduleAtFixedRate(() -> { dump(c); }, 0, DELAY, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        feederExecutor.shutdown();
    }
}
