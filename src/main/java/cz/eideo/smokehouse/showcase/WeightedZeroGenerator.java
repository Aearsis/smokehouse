package cz.eideo.smokehouse.showcase;

import java.util.Random;

/**
 * Generator that balances output numbers.
 * Every value the underlying generator outputs is
 * stored until it is subtracted again.
 */
public class WeightedZeroGenerator implements RealNumberGenerator {

    private RealNumberGenerator generator;

    private double[] storage = new double[256];

    Random r = new Random();

    public WeightedZeroGenerator(RealNumberGenerator generator) {
        this.generator = generator;
    }

    public double generateNext()
    {
        int index = r.nextInt(storage.length);
        double value = generator.generateNext() - storage[index];
        storage[index] += value;
        return value;
    }

}

