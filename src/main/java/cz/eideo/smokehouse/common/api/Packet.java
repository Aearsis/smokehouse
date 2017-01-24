package cz.eideo.smokehouse.common.api;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.io.*;

class Packet {

    /* These are not in enum because we want them to be sent as integers */
    public static final byte QUERY = 'Q';
    public static final byte VALUE = 'V';

    byte type;
    int key;

    private byte[] serialized;

    public Packet(byte type) {
        this.type = type;
    }

    public Packet(DataInputStream stream) throws IOException {
        read(stream);
    }

    public Packet(byte type, int key) {
        this(type);
        this.key = key;
    }

    void write(DataOutputStream os) throws IOException {
        os.writeByte(type);
        os.writeShort(key);
        if (type == VALUE) {
            os.writeShort(serialized.length);
            os.write(serialized);
        }
    }

    private void read(DataInputStream stream) throws IOException {
        type = stream.readByte();
        key = stream.readUnsignedShort();
        if (type == VALUE) {
            int len = stream.readUnsignedShort();
            serialized = new byte[len];
            stream.read(serialized);
        }
    }

    public static Packet valueFromNode(int key, Node node) throws IOException {
        Packet pkt = new Packet(VALUE, key);

        ByteArrayOutputStream buf = new ByteArrayOutputStream(256);
        DataOutputStream stream = new DataOutputStream(buf);
        node.writeValue(stream);
        pkt.serialized = buf.toByteArray();

        return pkt;
    }

    public void valueToNode(Node node) throws IOException {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(serialized));
        node.readValue(stream);
    }
}
