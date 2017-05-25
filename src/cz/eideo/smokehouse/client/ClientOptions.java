package cz.eideo.smokehouse.client;

import cz.cuni.mff.yaclpplib.Options;
import cz.eideo.smokehouse.common.api.MulticastEndpointOptions;

import java.net.UnknownHostException;

public class ClientOptions implements Options {

    public MulticastEndpointOptions multicastEndpointOptions = new MulticastEndpointOptions();

    public ClientOptions() throws UnknownHostException {
    }
}
