package cz.eideo.smokehouse.showcase;

import java.util.Random;

/**
 * Generator that generates numbers from normal distribution.
 */
public class GaussianGenerator implements RealNumberGenerator {

    /**
     * Generator will generate gaussian numbers with sigma and add them to the last generator.
     */
    private double sigma;
    private RealNumberGenerator generator;

    private Random r;


    public GaussianGenerator(double sigma, RealNumberGenerator generator) {
        r = new Random();
        this.sigma = sigma;
        this.generator = generator;
        generateNext();
    }

    public double generateNext()
    {
        return generator.generateNext() + (r.nextGaussian() * sigma);
    }

}
