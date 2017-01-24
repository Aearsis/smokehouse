package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.util.Observable;
import cz.eideo.smokehouse.common.util.Observer;

import java.security.InvalidParameterException;

/**
 * Simplifies creating simple observers.
 */
abstract public class RealNumberObserver implements Observer {

    protected RealNumberSource sensor;

    @Override
    public void attached(Observable s) {
        if (!(s instanceof RealNumberSource))
            throw new InvalidParameterException("Invalid observable.");
        this.sensor = (RealNumberSource) s;
    }
}
