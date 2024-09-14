package server;

import javax.swing.*;

public class Logger {
    private static JTextArea logArea;

    public static void setLogArea(JTextArea logArea) {
        Logger.logArea = logArea;
    }

    public static synchronized void log(String message) {
        if (logArea != null) {
            SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
        }
    }
}
