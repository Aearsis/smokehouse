package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.util.Observable;
import cz.eideo.smokehouse.common.util.Observer;

import java.security.InvalidParameterException;

/**
 * Simplifies creating simple observers.
 */
abstract public class RealNumberObserver implements Observer {

    protected StorageSource sensor;

    @Override
    public void attached(Observable s) {
        if (!(s instanceof StorageSource))
            throw new InvalidParameterException("Invalid observable.");
        this.sensor = (StorageSource) s;
    }
}
