package cz.eideo.smokehouse.common.api.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Effectively encodes thermal-like decimal in four bytes.
 */
public class ThermalCodec implements Codec<Double> {
    @Override
    public void encode(Double value, DataOutputStream stream) throws IOException {
        if (value <= 0)
            stream.writeShort(Short.MIN_VALUE);
        else if (value >= 256)
            stream.writeShort(Short.MAX_VALUE);
        else {
            int v = (int) (value * (1 << 8));
            stream.writeChar(v);
        }
    }

    @Override
    public Double decode(DataInputStream stream) throws IOException {
        int v = stream.readChar();
        return (double) v / (1 << 8);
    }
}
