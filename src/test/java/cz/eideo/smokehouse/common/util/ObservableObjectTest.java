package cz.eideo.smokehouse.common.util;

import org.junit.Assert;
import org.junit.Test;

public class ObservableObjectTest {

    static class SimpleObservable extends ObservableObject {
        public void update() {
            signalMonitors();
        }
    }

    boolean attached = false;
    boolean signaled = false;

    @Test
    public void testMonitoring() throws Exception {
        Observer mockMonitor = new Observer() {
            @Override
            public void attached(Observable s) {
                attached = true;
            }

            @Override
            public void signal() {
                signaled = true;
            }
        };

        SimpleObservable s = new SimpleObservable();
        s.attachObserver(mockMonitor);
        Assert.assertTrue(attached);

        s.update();
        Assert.assertTrue(signaled);
    }


}