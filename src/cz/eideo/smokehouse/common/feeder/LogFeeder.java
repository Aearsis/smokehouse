package cz.eideo.smokehouse.common.feeder;

import cz.eideo.smokehouse.common.event.Event;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.setup.ThermometerArray;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parses input stream as a logfile from previous generations of smokehouse frontend.
 *
 * The format is:
 * (date and time) t1 t2 ... t18
 * ...
 *
 * This feeder reads the first line, sets the time offset,
 * and then continues to emit values with delays between them.
 *
 * So, this feeder behaves exactly like the real feeder would in the real scenario.
 */
public class LogFeeder implements Runnable {

    final static int count = 18;
    final static SimpleDateFormat inputDateFormat = new SimpleDateFormat("Y-M-d H:m:s");

    final BufferedReader input;
    final ThermometerArray thermometers;

    final long replaySpeedup;

    final static DateTimeFormatter outputFormatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.FULL)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    private final Event updateEvent;

    private static class DataRow {
        Instant time;
        Double[] values;

        DataRow(String line) throws FormatException {
            String[] split = line.split("\t");
            if (split.length != count + 1)
                throw new FormatException("Invalid number of fields.");

            try {
                time = inputDateFormat.parse(split[0]).toInstant();
            } catch (ParseException e) {
                throw new FormatException("Cannot parse time: " + e.getMessage());
            }

            try {
                values = new Double[count];
                for (int i = 0; i < count; i++)
                    values[i] = Double.parseDouble(split[i + 1]);
            } catch (NumberFormatException e) {
                throw new FormatException("Cannot parse thermal value: "  + e.getMessage());
            }
        }
    }


    public LogFeeder(InputStream input, ThermometerArray thermometers, long replaySpeedup, EventFactory eventFactory) {
        this.input = new BufferedReader(new InputStreamReader(input));
        this.thermometers = thermometers;
        this.replaySpeedup = replaySpeedup;

        this.updateEvent = eventFactory.createEvent(this::feed);
    }

    private Instant offset;
    private Instant started;
    private DataRow nextRow;

    private void feed() {
        emitValues(nextRow);
        getLogger().log(Level.INFO, "Fed values from " + outputFormatter.format(nextRow.time));

        try {
            if (input.ready()) {
                nextRow = new DataRow(input.readLine());

                final Instant at = started.plus(Duration.between(offset, nextRow.time).dividedBy(replaySpeedup));
                updateEvent.scheduleTo(at);
            } else {
                logger.log(Level.INFO, "Finished feeding. Good apetite!");
            }
        } catch (IOException | FormatException e) {
            getLogger().log(Level.SEVERE, "An error occured while reading the logfile. Feeding will stop.", e);
        }
    }

    @Override
    public void run() {
        try {
            nextRow = new DataRow(input.readLine());
            offset = nextRow.time;
            started = Instant.now();

            getLogger().log(Level.INFO, "Started feeding from log file.");
            getLogger().log(Level.INFO, "Replayed smoking started at " + outputFormatter.format(offset));

            updateEvent.schedule();
        } catch (FormatException | IOException e) {
            getLogger().log(Level.SEVERE, "An error occured while reading the logfile. Feeding will stop.", e);
        }
    }

    final static Logger logger = Logger.getLogger(LogFeeder.class.getName());

    private Logger getLogger() {
        return logger;
    }

    private void emitValues(final DataRow dataRow) {
        for (int i = 0; i < count; i++) {
            thermometers.getThermometer(i).updateValue(dataRow.values[i]);
        }
    }

    private static class FormatException extends Exception {
        public FormatException(String message) {
            super(message);
        }
    }
}
