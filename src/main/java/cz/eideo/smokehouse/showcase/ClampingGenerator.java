package cz.eideo.smokehouse.showcase;

import java.util.Map;

/**
 * Generator that clamps values from another generator.
 * Values are not just clamped, they are offset so that generator
 * does not slide to bad values for too long time.
 */
public class ClampingGenerator implements RealNumberGenerator {

    private double min;
    private double max;
    private RealNumberGenerator generator;

    public ClampingGenerator(double min, double max, RealNumberGenerator generator) {
        this.min = min;
        this.max = max;
        this.generator = generator;
    }

    @Override
    public double generateNext() {
        double value = generator.generateNext();
        return Math.min(max, Math.max(min, value));
    }
}
