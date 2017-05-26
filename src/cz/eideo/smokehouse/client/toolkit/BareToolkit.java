package cz.eideo.smokehouse.client.toolkit;

import java.io.PrintWriter;

/**
 * ConsoleToolkit for text outputs without colors.
 */
class BareToolkit implements ConsoleToolkit {

    final PrintWriter writer;

    BareToolkit(PrintWriter writer) {
        this.writer = writer;
        clearScreen();
    }

    boolean lineStarted = false;
    String linePrefix;

    @Override
    public void clearScreen() {
        for (int i = 0; i < 80; i++)
            nextLine();
    }

    @Override
    public void goHome() {
        clearScreen();
        lineStarted = false;
    }

    /**
     * Print the prefix, if not yet printed on this line.
     */
    protected void startLine() {
        if (lineStarted || linePrefix == null)
            return;

        writer.print(linePrefix);
        lineStarted = true;
    }

    @Override
    public void setPrefix(String prefix) {
        linePrefix = prefix;
    }

    @Override
    public void nextLine() {
        startLine();
        writer.println();
        lineStarted = false;
    }

    @Override
    public void printf(String format, Object... args) {
        startLine();
        writer.printf(format, args);
    }

    @Override
    public void flush() {
        writer.flush();
    }



}
