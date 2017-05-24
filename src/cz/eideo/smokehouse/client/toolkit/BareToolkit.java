package cz.eideo.smokehouse.client.toolkit;

import java.io.PrintWriter;

/**
 * ConsoleToolkit for text outputs without colors.
 */
class BareToolkit extends ConsoleToolkit {

    PrintWriter writer;

    public void flush() {
        writer.flush();
    }

    public BareToolkit(PrintWriter writer) {
        this.writer = writer;
        clearScreen();
    }

    boolean lineStarted = false;
    String linePrefix;

    public void clearScreen() {
        for (int i = 0; i < 80; i++)
            nextLine();
    }

    public void goHome() {
        clearScreen();
        lineStarted = false;
    }

    private void startLine() {
        if (lineStarted || linePrefix == null)
            return;

        writer.print(linePrefix);
        lineStarted = true;
    }

    public void setPrefix(String prefix) {
        linePrefix = prefix;
    }

    public void nextLine() {
        startLine();
        writer.println();
        lineStarted = false;
    }

    public void printf(String format, Object... args) {
        startLine();
        writer.printf(format, args);
    }

    public void setColor(Color color) {
    }

    public void resetColor() {
    }


}
