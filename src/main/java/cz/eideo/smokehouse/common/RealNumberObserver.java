package cz.eideo.smokehouse.common;

import cz.eideo.smokehouse.common.util.Observable;
import cz.eideo.smokehouse.common.util.Observer;

import java.security.InvalidParameterException;

abstract public class RealNumberObserver implements Observer {

    protected RealNumberSensor sensor;

    @Override
    public void attached(Observable s) {
        if (!(s instanceof RealNumberSensor))
            throw new InvalidParameterException("Invalid observable.");
        this.sensor = (RealNumberSensor) s;
    }
}
