package cz.eideo.smokehouse.common.api.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EnumCodec<T extends Enum<T>> implements Codec<T> {

    final Class<T> enumType;

    public EnumCodec(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public void encode(T value, DataOutputStream stream) throws IOException {
        stream.writeUTF(value.name());
    }

    @Override
    public T decode(DataInputStream stream) throws IOException {
        return Enum.valueOf(enumType, stream.readUTF());
    }
}
