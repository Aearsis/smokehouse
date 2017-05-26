package cz.eideo.smokehouse.common.feeder;

import cz.eideo.smokehouse.common.event.Event;
import cz.eideo.smokehouse.common.event.EventFactory;
import cz.eideo.smokehouse.common.sensor.ThermoArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parses input stream as a logfile from previous generations of smokehouse frontend.
 * <p>
 * The format is:
 * (date and time) t1 t2 ... t18
 * ...
 * <p>
 * This feeder reads the first line, sets the time offset,
 * and then continues to emit values with delays between them.
 * <p>
 * So, this feeder behaves exactly like the real feeder would in the real scenario.
 */
public class LogFeeder implements Runnable {

    private final static Logger logger = Logger.getLogger(LogFeeder.class.getName());

    private final static int count = 18;
    private final static SimpleDateFormat logDateFormat = new SimpleDateFormat("Y-M-d H:m:s");

    private final static DateTimeFormatter outputDateFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withZone(ZoneId.systemDefault());
    private final BufferedReader input;
    private final ThermoArea thermometers;

    private final long replaySpeedup;

    private final Event updateEvent;

    private static class DataRow {
        Date time;
        Double[] values;

        DataRow(String line) throws FormatException {
            String[] split = line.split("\t");
            if (split.length != count + 1)
                throw new FormatException("Invalid number of fields.");

            try {
                time = logDateFormat.parse(split[0]);
            } catch (ParseException e) {
                throw new FormatException("Cannot parse time: " + e.getMessage());
            }

            try {
                values = new Double[count];
                for (int i = 0; i < count; i++)
                    values[i] = Double.parseDouble(split[i + 1]);
            } catch (NumberFormatException e) {
                throw new FormatException("Cannot parse thermal value: " + e.getMessage());
            }
        }
    }


    public LogFeeder(InputStream input, ThermoArea thermometers, long replaySpeedup, EventFactory eventFactory) {
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
        logger.log(Level.INFO, "Fed values from " + nextRow.time.toString());

        try {
            if (input.ready()) {
                nextRow = new DataRow(input.readLine());

                final Instant at = started.plus(Duration.between(offset, nextRow.time.toInstant()).dividedBy(replaySpeedup));
                updateEvent.scheduleTo(at);
            } else {
                logger.log(Level.INFO, "Finished feeding. Good apetite!");
            }
        } catch (IOException | FormatException e) {
            logger.log(Level.SEVERE, "An error occured while reading the logfile. Feeding will stop.", e);
        }
    }

    @Override
    public void run() {
        try {
            nextRow = new DataRow(input.readLine());
            offset = nextRow.time.toInstant();
            started = Instant.now();

            logger.log(Level.INFO, "Started feeding from log file.");
            logger.log(Level.INFO, "Replayed smoking started at " + nextRow.time.toString());

            updateEvent.schedule();
        } catch (FormatException | IOException e) {
            logger.log(Level.SEVERE, "An error occured while reading the logfile. Feeding will stop.", e);
        }
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
