package cz.eideo.smokehouse.client.toolkit;

import java.io.PrintWriter;

/**
 * Simplify using features of rich output - colors, cursor movement.
 * <p>
 * Also, enable abstraction so that using classes must not care about whether output supports features.
 */
public interface ConsoleToolkit {

    /**
     * @return the best toolkit for current output
     */
    static ConsoleToolkit getToolkit() {
        if (System.console() == null) {
            return new BareToolkit(new PrintWriter(System.out));
        } else {
            return new TerminalToolkit(System.console().writer());
        }
    }

    /**
     * Colors for terminal output
     */
    enum Color {
        BLACK("\u001B[30m"), RED("\u001B[31m"), GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"), BLUE("\u001B[34m"), PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"), WHITE("\u001B[37m"), DEFAULT("\u001B[0m");

        final String code;

        Color(String c) {
            code = c;
        }
    }

    /**
     * Clear all that is written on the screen.
     */
    void clearScreen();

    /**
     * Move the cursor to a fixed place (usually top left corner)
     */
    void goHome();

    /**
     * Advance to the next line.
     */
    void nextLine();

    /**
     * Flush the output.
     */
    void flush();

    /**
     * Set a prefix.
     * @param prefix a string that will precede every line on output
     */
    void setPrefix(String prefix);

    /**
     * Standard printf. Do not use newline if you use prefixes.
     * @param format printf format string
     * @param args arguments for printf
     */
    void printf(String format, Object... args);

    /**
     * Set the color of font (only if the terminal supports it).
     * @param color Color to set
     */
    default void setColor(Color color) {}

    /**
     * Reset to the default color.
     */
    default void resetColor() {}

}
