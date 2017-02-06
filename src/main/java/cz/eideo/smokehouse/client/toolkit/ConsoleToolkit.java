package cz.eideo.smokehouse.client.toolkit;

import java.io.PrintWriter;

public abstract class ConsoleToolkit {

    public final static ConsoleToolkit getToolkit() {
        if (System.console() == null) {
            return new BareToolkit(new PrintWriter(System.out));
        } else {
            return new TerminalToolkit(System.console().writer());
        }
    }

    public enum Color {
         BLACK("\u001B[30m"), RED("\u001B[31m"), GREEN("\u001B[32m"),
         YELLOW("\u001B[33m"), BLUE("\u001B[34m"), PURPLE("\u001B[35m"),
         CYAN("\u001B[36m"), WHITE("\u001B[37m"), DEFAULT("\u001B[0m");

         String code;
         Color (String c) { code = c; }
    }

    abstract public void clearScreen();
    abstract public void goHome();
    abstract public void nextLine();
    abstract public void flush();

    abstract public void setPrefix(String prefix);
    abstract public void printf(String format, Object... args);

    abstract public void setColor(Color color);
    abstract public void resetColor();

}
