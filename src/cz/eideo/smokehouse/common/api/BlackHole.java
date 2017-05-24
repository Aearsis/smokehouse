package cz.eideo.smokehouse.common.api;

/**
 * Dummy API Endpoint that will never send nor receive values.
 */
public enum BlackHole implements Endpoint {

    SINGULARITY;

    @Override
    public int addNode(Node n) {
        return 0;
    }

}
