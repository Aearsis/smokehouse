package cz.eideo.smokehouse.showcase;

/**
 * Generator that limits values from another generator.
 * Values are not just clamped, they are offset so that generator
 * does not slide to bad values for too long time.
 */
public class LimitingGenerator implements RealNumberGenerator {

    private final double min;
    private final double max;
    private double offset = 0;
    private final RealNumberGenerator generator;

    public LimitingGenerator(double min, double max, RealNumberGenerator generator) {
        this.min = min;
        this.max = max;
        this.generator = generator;
    }

    @Override
    public double generateNext() {
        double value = generator.generateNext();

        if (value + offset > max)
            offset = max - value;
        else if (value + offset < min)
            offset = min - value;

        return value + offset;
    }
}
