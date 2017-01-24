package cz.eideo.smokehouse.showcase;

import cz.eideo.smokehouse.common.RealNumberFeeder;

/**
 * Feeds the sensor with random values whenever emit is called.
 */
public class RealNumberRandomFeeder extends RealNumberFeeder {

    private RealNumberGenerator generator;

    public RealNumberRandomFeeder(RealNumberGenerator generator) {
        this.generator = generator;
    }

    public void emit() {
        sensor.setValue(generator.generateNext());
    }
}
