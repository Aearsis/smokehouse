package cz.eideo.smokehouse.common.api;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class Packet implements Comparable<Packet> {

    /* These are not in enum because we want them to be sent as integers */
    static final byte QUERY = 'Q';
    static final byte VALUE = 'V';

    byte type;
    int key;

    /* Used when writing only */
    private byte[] serialized;

    Packet(byte type, int key) {
        this.type = type;
        this.key = key;
    }

    Packet(DataInputStream stream) throws IOException {
        type = stream.readByte();
        key = stream.readByte();
    }

    void write(DataOutputStream os) throws IOException {
        os.writeByte(type);
        os.writeByte(key);
        if (type == VALUE) {
            os.write(serialized);
        }
    }


    static Packet valueFromNode(int key, Node node) throws IOException {
        Packet pkt = new Packet(VALUE, key);

        ByteArrayOutputStream buf = new ByteArrayOutputStream(256);
        DataOutputStream stream = new DataOutputStream(buf);
        node.writeValueTo(stream);
        pkt.serialized = buf.toByteArray();

        return pkt;
    }

    @Override
    public int compareTo(Packet other) {
        if (this.type != other.type)
            return this.type - other.type;
        if (this.key != other.key)
            return this.key - other.key;
        return 0;
    }
}
