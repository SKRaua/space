package client;

import javax.swing.*;
import java.awt.*;

public class ExceptionGUI extends JFrame {
    private JLabel eLabel;
    private JButton eButton;

    /**
     * 配置连接异常状态时的UI，传入地址用于重连
     * 
     * @param serverAddress
     * @param serverPort
     */
    public ExceptionGUI(String serverAddress, int serverPort) {
        super("SKRaua聊天室？");
        eLabel = new JLabel("*连接异常*", SwingConstants.CENTER);
        eButton = new JButton("重试连接");

        eButton.addActionListener(e -> {
            this.dispose();
            new ClientGUI(serverAddress, serverPort);
        });

        // 上方提示，下方重连按钮
        this.getContentPane().add(eLabel, BorderLayout.CENTER);
        this.getContentPane().add(eButton, BorderLayout.SOUTH);

        this.pack();// 窗口自适应大小
        this.setSize(280, 160);
        this.setLocationRelativeTo(null);// 设置位置（到屏幕中间）
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
