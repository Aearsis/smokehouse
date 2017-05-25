package cz.eideo.smokehouse.client;

import cz.eideo.smokehouse.common.setup.CubeSetup;
import cz.eideo.smokehouse.common.sensor.*;
import cz.eideo.smokehouse.common.statistics.*;

import cz.eideo.smokehouse.client.toolkit.ConsoleToolkit;

import java.util.Arrays;

/**
 * This is a simple dumper of temperatures, made for the CubeSetup.
 */
class CubeConsoleDumper {

    private final ConsoleToolkit toolkit;
    private final CubeSetup setup;
    private final ThermoArea cubeArea, topPlane, bottomPlane;

    /**
     * Formats for printing - keep them the same fixed width
     */
    private static final String thermo_format = "    %5.1f ";
    private static final String avg_format = "   (%5.1f)";

    CubeConsoleDumper(CubeSetup setup) {
        this.toolkit = ConsoleToolkit.getToolkit();
        this.setup = setup;

        cubeArea = setup.getCubeArea();
        topPlane = setup.getPlaneArea(0);
        bottomPlane = setup.getPlaneArea(1);
    }

    /**
     * Print current state of thermometers to the console
     */
    void dump() {
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

        printLine(0, 3);
        printLine(3, 2);
        printLine(6, 1);
        toolkit.nextLine();
        toolkit.nextLine();

        printLine(9, 3);
        printLine(12, 2);
        printLine(15, 1);
        toolkit.goHome();
        toolkit.flush();
    }

    /**
     * Print statistics of one area
     *
     * @param area the area to dump
     */
    private void areaTemperatures(ThermoArea area) {
        autoDiffColor(area.slidingAverage.waitForValue(), area.average.waitForValue());
        toolkit.printf("%5.1f", area.average.waitForValue());
        toolkit.resetColor();
        toolkit.printf(", ");
        toolkit.setColor(ConsoleToolkit.Color.DEFAULT);
        autoColor(area.slidingAverage.waitForValue());
        toolkit.printf("(%5.1f)", area.slidingAverage.waitForValue());
        toolkit.resetColor();
    }


    /**
     * Print one first of thermometers
     *
     * @param first index of first thermometer in the line
     * @param z     depth of this line (for 3D effect)
     */
    private void printLine(int first, int z) {
        toolkit.setPrefix(space(4 * z));
        for (int i = first; i < first + 3; i++) {
            Thermometer t = setup.getThermometer(i);
            SlidingAverage<Double> a = t.slidingAverage;
            autoDiffColor(a.waitForValue(), t.waitForValue());
            toolkit.printf(thermo_format, t.waitForValue());
        }
        toolkit.nextLine();

        for (int i = first; i < first + 3; i++) {
            Thermometer t = setup.getThermometer(i);
            SlidingAverage<Double> a = t.slidingAverage;
            toolkit.setColor(ConsoleToolkit.Color.YELLOW);
            autoColor(a.waitForValue());
            toolkit.printf(avg_format, a.waitForValue());
        }
        toolkit.nextLine();

        toolkit.resetColor();
        toolkit.setPrefix("");
        toolkit.nextLine();
        toolkit.nextLine();
    }

    /**
     * @param len length of the generated space
     * @return string of spaces of length len
     */
    private String space(int len) {
        char[] space = new char[len];
        Arrays.fill(space, ' ');
        return new String(space);
    }

    /**
     * Sets the color on toolkit based on temperature difference.
     * Red = getting warmer
     * Blue = getting colder
     *
     * @param old old temperature
     * @param cur new temperature
     */
    private void autoDiffColor(Double old, Double cur) {
        if (cur > old)
            toolkit.setColor(ConsoleToolkit.Color.RED);
        else
            toolkit.setColor(ConsoleToolkit.Color.BLUE);
    }

    /**
     * Set the color on toolkit based on temperature
     * Red = if too hot
     * Blue = if too cold
     * ... for smoking sausages.
     *
     * @param value the temperature
     */
    private void autoColor(Double value) {
        if (value <= 70)
            toolkit.setColor(ConsoleToolkit.Color.BLUE);
        else if (value >= 90)
            toolkit.setColor(ConsoleToolkit.Color.RED);
    }
}
