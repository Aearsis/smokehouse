package cz.eideo.smokehouse.common.util;

import sun.misc.Signal;

public class Posix {

    public static void waitForSigterm(Runnable callback) {
        Signal.handle(new Signal("TERM"), signal -> callback.run());
    }
}
