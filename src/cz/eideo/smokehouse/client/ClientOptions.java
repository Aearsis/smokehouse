package cz.eideo.smokehouse.client;

import cz.cuni.mff.yaclpplib.Options;
import cz.cuni.mff.yaclpplib.annotation.Help;
import cz.eideo.smokehouse.common.api.MulticastEndpointOptions;

import java.net.UnknownHostException;

/**
 * COmmand-line options for the CLI.
 */
@Help("Options for the command line client")
class ClientOptions implements Options {

    final MulticastEndpointOptions multicastEndpointOptions = new MulticastEndpointOptions();

    ClientOptions() throws UnknownHostException { }
}
