package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.api.Endpoint;
import cz.eideo.smokehouse.common.api.Node;
import cz.eideo.smokehouse.common.api.StorageNode;
import cz.eideo.smokehouse.common.util.ObservableObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;

/**
 * Double value source, which stores its value into a storage node.
 */
abstract public class RealNumberSource extends ObservableObject implements Source<Double> {

    private StorageNode<Double> node = new StorageNode<Double>() {
        @Override
        public void writeValue(DataOutputStream stream) throws IOException {
            stream.writeDouble(getValue());
        }

        @Override
        public void readValue(DataInputStream dataStream) throws IOException {
            setValue(dataStream.readDouble());
        }

        @Override
        public synchronized void setValue(Double value, long validMillis) {
            super.setValue(value, validMillis);
            signalMonitors();
        }
    };

    public Double getValue() {
        return node.getValue();
    }

    @Override
    public Double getTimedValue(Instant time) {
        return null;
    }

    public void setValue(double value) {
        node.setValue(value);
    }

    @Override
    public void attachAPI(Endpoint api) {
        node.attachAPI(api);
    }

}
