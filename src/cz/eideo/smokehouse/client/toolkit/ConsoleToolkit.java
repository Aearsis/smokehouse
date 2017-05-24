package cz.eideo.smokehouse.client.toolkit;

import java.io.PrintWriter;

/**
 * Simplify using features of rich output - colors, cursor movement.
 * <p>
 * Also, enable abstraction so that using classes must not care about whether output supports features.
 */
public abstract class ConsoleToolkit {

    /**
     * @return the best toolkit for current output
     */
    public static ConsoleToolkit getToolkit() {
        if (System.console() == null) {
            return new BareToolkit(new PrintWriter(System.out));
        } else {
            return new TerminalToolkit(System.console().writer());
        }
    }

    /**
     * Colors for terminal output
     */
    public enum Color {
        BLACK("\u001B[30m"), RED("\u001B[31m"), GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"), BLUE("\u001B[34m"), PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"), WHITE("\u001B[37m"), DEFAULT("\u001B[0m");

        final String code;

        Color(String c) {
            code = c;
        }
    }

    abstract public void clearScreen();

    /**
     * Move the cursor to a fixed place (usually top left corner)
     */
    abstract public void goHome();

    abstract public void nextLine();

    abstract public void flush();

    abstract public void setPrefix(String prefix);

    abstract public void printf(String format, Object... args);

    abstract public void setColor(Color color);

    abstract public void resetColor();

}
