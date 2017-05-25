package cz.eideo.smokehouse.client;

import cz.cuni.mff.yaclpplib.Options;
import cz.cuni.mff.yaclpplib.annotation.Help;
import cz.cuni.mff.yaclpplib.annotation.Option;
import cz.eideo.smokehouse.common.api.MulticastEndpointOptions;

import java.net.UnknownHostException;

public class ClientOptions implements Options {

    public MulticastEndpointOptions multicastEndpointOptions = new MulticastEndpointOptions();

    @Option("--delay")
    @Option("-d")
    @Help("The values will be dumped every d milliseconds")
    public long dumpRate = 5000;

    public ClientOptions() throws UnknownHostException {
    }
}
