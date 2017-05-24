package cz.eideo.smokehouse.showcase;

import java.util.Random;

/**
 * Generator that generates numbers from normal distribution.
 */
public class GaussianGenerator implements RealNumberGenerator {

    /**
     * Generator will generate gaussian numbers with specified
     * variability and add them to the last generator.
     */
    private final double var;
    private final RealNumberGenerator generator;

    private final Random r;


    public GaussianGenerator(double var, RealNumberGenerator generator) {
        r = new Random();
        this.var = var;
        this.generator = generator;
    }

    public double generateNext() {
        return generator.generateNext() + (r.nextGaussian() * var);
    }

}
