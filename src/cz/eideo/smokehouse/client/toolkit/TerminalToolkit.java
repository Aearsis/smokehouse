package cz.eideo.smokehouse.client.toolkit;

import java.io.PrintWriter;

/**
 * Output for xterm terminals supporting colors and cursor movement.
 */
class TerminalToolkit extends BareToolkit {

    TerminalToolkit(PrintWriter writer) {
        super(writer);
    }

    private Color currentColor = Color.DEFAULT;

    @Override
    public void clearScreen() {
        writer.print("\033[2J");
        lineStarted = false;
    }

    @Override
    public void goHome() {
        writer.print("\033[H");
        lineStarted = false;
    }

    @Override
    protected void startLine() {
        if (lineStarted || linePrefix == null)
            return;

        writer.print(Color.DEFAULT.code + linePrefix + currentColor.code);
        lineStarted = true;
    }

    @Override
    public void setColor(Color color) {
        if (currentColor == color)
            return;

        writer.print(color.code);
        currentColor = color;
    }

    @Override
    public void resetColor() {
        setColor(Color.DEFAULT);
    }

    @Override
    public void flush() {
        writer.flush();
    }


}
