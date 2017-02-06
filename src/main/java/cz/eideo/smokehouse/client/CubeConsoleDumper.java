package cz.eideo.smokehouse.client;

import cz.eideo.smokehouse.client.toolkit.ConsoleToolkit;
import cz.eideo.smokehouse.common.sensor.ThermoArea;
import cz.eideo.smokehouse.common.sensor.Thermometer;
import cz.eideo.smokehouse.common.setup.CubeSetup;
import cz.eideo.smokehouse.common.statistics.SlidingAverage;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This is a simple dumper of temperatures, made for the CubeSetup.
 */
public class CubeConsoleDumper {

    private static final long DELAY = 1000;
    private final ConsoleToolkit toolkit;
    private final CubeSetup setup;

    private final ThermoArea cubeArea, topPlane, bottomPlane;

    // Keep these the same width
    private static final String thermo_format = "    %5.1f ";
    private static final String avg_format    = "   (%5.1f)";

    public CubeConsoleDumper(CubeSetup setup) {
        this.toolkit = ConsoleToolkit.getToolkit();
        this.setup = setup;

        cubeArea = setup.getCubeArea();
        bottomPlane = setup.getPlaneArea(0);
        topPlane = setup.getPlaneArea(1);
    }

    private void dump() {
        toolkit.goHome();


        toolkit.printf("          Top: ");
        areaTemperatures(topPlane);
        toolkit.nextLine();
        toolkit.printf("       Bottom: ");
        areaTemperatures(bottomPlane);
        toolkit.nextLine();
        toolkit.printf("     Together: ");
        areaTemperatures(cubeArea);
        toolkit.nextLine();
        toolkit.nextLine();
        toolkit.nextLine();

        printLine(11, 3);
        printLine(10, 2);
        printLine(9, 1);
        toolkit.nextLine();
        toolkit.nextLine();

        printLine(2, 3);
        printLine(1, 2);
        printLine(0, 1);
        toolkit.flush();

    }

    private void areaTemperatures(ThermoArea area) {
        autoDiffColor(area.slidingAverage.getValue(), area.average.getValue());
        toolkit.printf("%5.1f", area.average.getValue());
        toolkit.resetColor();
        toolkit.printf(", ");
        autoColor(area.slidingAverage.getValue(), ConsoleToolkit.Color.DEFAULT);
        toolkit.printf("(%5.1f)", area.slidingAverage.getValue());
        toolkit.resetColor();
    }


    private void printLine(int line, int z) {
        toolkit.setPrefix(space(4 * z));
        for (int i = line; i < line + 9; i += 3) {
            Thermometer t = setup.getThermometer(i);
            SlidingAverage a = t.slidingAverage;
            autoDiffColor(a.getValue(), t.getValue());
            toolkit.printf(thermo_format, t.getValue());
        }
        toolkit.nextLine();

        for (int i = line; i < line + 9; i += 3) {
            Thermometer t = setup.getThermometer(i);
            SlidingAverage a = t.slidingAverage;
            autoColor(a.getValue(), ConsoleToolkit.Color.YELLOW);
            toolkit.printf(avg_format, a.getValue());
        }
        toolkit.nextLine();

        toolkit.resetColor();
        toolkit.setPrefix("");
        toolkit.nextLine();
        toolkit.nextLine();
    }

    private String space(int len) {
        char[] space = new char[len];
        Arrays.fill(space, ' ');
        return new String(space);
    }

    private void autoDiffColor(Double old, Double cur) {
        if (cur > old)
            toolkit.setColor(ConsoleToolkit.Color.RED);
        else
            toolkit.setColor(ConsoleToolkit.Color.BLUE);
    }

    private void autoColor(Double value, ConsoleToolkit.Color defColor) {
        if (value <= 70)
            toolkit.setColor(ConsoleToolkit.Color.BLUE);
        else if (value >= 90)
            toolkit.setColor(ConsoleToolkit.Color.RED);
        else
            toolkit.setColor(defColor);
    }

    ScheduledExecutorService feederExecutor;

    public void start() {
        feederExecutor = Executors.newSingleThreadScheduledExecutor();
        feederExecutor.scheduleAtFixedRate(() -> {
            try {
                dump();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, DELAY, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        feederExecutor.shutdown();
    }
}
