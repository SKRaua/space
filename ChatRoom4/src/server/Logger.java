package server;

import javax.swing.*;

public class Logger {
    private static JTextArea logArea;

    public static void setLogArea(JTextArea logArea) {
        Logger.logArea = logArea;
    }

    /**
     * 多线程会输出日志，同步机制来输出日志到服务器终端窗口
     * 
     * @param message
     */
    public static synchronized void log(String message) {
        if (logArea != null) {
            SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
        }
    }
}
