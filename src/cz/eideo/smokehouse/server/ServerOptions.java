package cz.eideo.smokehouse.server;

import cz.cuni.mff.yaclpplib.Options;
import cz.cuni.mff.yaclpplib.annotation.AfterParse;
import cz.cuni.mff.yaclpplib.annotation.Help;
import cz.cuni.mff.yaclpplib.annotation.Mandatory;
import cz.cuni.mff.yaclpplib.annotation.Option;
import cz.eideo.smokehouse.common.api.MulticastEndpointOptions;

import java.io.File;
import java.io.FileInputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Help("Options for the server")
class ServerOptions implements Options {

    final MulticastEndpointOptions multicastEndpointOptions = new MulticastEndpointOptions();

    ServerOptions() throws UnknownHostException { }

    @Option("--db")
    @Help("A SQLite database to store the values.")
    File dbName = new File(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "-smoking.db");

    @Mandatory
    @Option("--replay")
    @Help("The logfile to read and simulate.")
    FileInputStream logStream;

    @Option(value = "--replay-speedup", help = "--replay-speedup=R")
    @Help("The log will be replayed R times faster (default 1)")
    long replaySpeedup = 1;

}
