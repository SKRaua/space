package server;

import java.io.IOException;

import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {
    private ChatServer server;
    private JTextField urlField;
    private JTextField userField;
    private JTextField passwordField;
    private JTextArea logArea;
    // private JButton sendButton;
    private JButton connectButton;

    public ServerGUI(String url, String user, String passsword) {
        super("SKRaua聊天室服务终端");
        buildGUI(url, user, passsword);// 构建窗口UI
        this.server = new ChatServer(url, user, passsword);
    }

    /**
     * 配置客户端UI，监听发送信息的动作
     */
    private void buildGUI(String url, String user, String passsword) {
        // 聊天室，输入框，聊天区域，发送按钮
        logArea = new JTextArea(20, 40);
        logArea.setEditable(false);
        urlField = new JTextField(url, 40);
        userField = new JTextField(user, 40);
        passwordField = new JTextField(passsword, 40);
        connectButton = new JButton("连接数据库");

        // // 为输入框和发送按钮添加监听用于发送信息
        // inputField.addActionListener(e -> sendMessage());
        // sendButton.addActionListener(e -> sendMessage());
        String urlLink = urlField.getText();
        String userLink = userField.getText();
        String passswordLink = passwordField.getText();
        connectButton.addActionListener(e -> server.connectToDatabase(urlLink, userLink, passswordLink));

        // 设置窗口的布局管理器
        setLayout(new BorderLayout());
        // 边缘布局，上方日志输出区域（使用滚动面板），下方输入框和发送按钮
        this.getContentPane().add(new JScrollPane(logArea), BorderLayout.CENTER);
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(3, 1));
        // 添加输入框到面板
        fieldPanel.add(urlField);
        fieldPanel.add(userField);
        fieldPanel.add(passwordField);

        // 创建面板用于按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(connectButton);

        // 创建一个容器面板来包含输入框面板和按钮面板
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(fieldPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.EAST);

        // 添加容器面板到窗口的底部
        add(southPanel, BorderLayout.SOUTH);

        this.pack();// 窗口自适应大小
        this.setLocationRelativeTo(null);// 设置位置（到屏幕中间）
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

}
