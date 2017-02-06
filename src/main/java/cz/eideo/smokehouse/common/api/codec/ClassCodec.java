package cz.eideo.smokehouse.common.api.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Codec for class names.
 */
public class ClassCodec implements Codec<Class> {
    @Override
    public void encode(Class value, DataOutputStream stream) throws IOException {
        stream.writeUTF(value.getCanonicalName());

    }

    @Override
    public Class decode(DataInputStream stream) throws IOException {
        String canonicalName = stream.readUTF();
        try {
            return Class.forName(canonicalName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new Error("Cannot continue.");
        }
    }
}
