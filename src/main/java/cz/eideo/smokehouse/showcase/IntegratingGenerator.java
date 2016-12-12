package cz.eideo.smokehouse.showcase;

/**
 * Generator that integrates another generator.
 */
public class IntegratingGenerator implements RealNumberGenerator {

    private double value;
    private RealNumberGenerator generator;

    public IntegratingGenerator(double initial, RealNumberGenerator generator) {
        value = initial;
        this.generator = generator;
    }

    @Override
    public double generateNext() {
        value += generator.generateNext();
        return value;
    }
}
