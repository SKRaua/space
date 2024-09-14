package client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * 客户端UI窗口类
 */
public class ChatClientGUI extends JFrame {
    private ChatClient client;
    private JTextField inputField;
    private JTextArea chatArea;
    private JButton sendButton;

    public ChatClientGUI(String serverAddress, int serverPort) {
        super("SKRaua聊天室");
        try {
            this.client = new ChatClient(serverAddress, serverPort);
            buildGUI();// 构建窗口UI，监听发送信息动作
            startMessageReceiver();// 启动接收信息的线程
        } catch (IOException e) {
            e.printStackTrace();
            exceptionUI(serverAddress, serverPort);// 构建连接异常时的UI
        }
    }

    /**
     * 配置客户端UI，监听发送信息的动作
     */
    private void buildGUI() {
        this.getContentPane().removeAll();// 清空窗体
        // 聊天室，输入框，聊天区域，发送按钮
        inputField = new JTextField(40);
        chatArea = new JTextArea(20, 40);
        chatArea.setEditable(false);
        sendButton = new JButton("发送");

        // 为输入框和发送按钮添加监听用于发送信息
        inputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        // 边缘布局，上方聊天区域（使用滚动面板），下方输入框和发送按钮
        this.getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);
        this.getContentPane().add(panel, BorderLayout.SOUTH);

        this.pack();// 窗口自适应大小
        this.setLocationRelativeTo(null);// 设置位置（到屏幕中间）
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * 发送信息
     */
    private void sendMessage() {
        try {
            String message = inputField.getText();
            client.sendMessage(message);
            inputField.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动接收信息的线程
     */
    private void startMessageReceiver() {
        new Thread(() -> {
            try {// 循环读取收到的信息
                String message;
                while ((message = client.receiveMessage()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 配置连接异常状态时的UI，传入地址用于重连
     * 
     * @param serverAddress
     * @param serverPort
     */
    private void exceptionUI(String serverAddress, int serverPort) {
        this.getContentPane().removeAll();
        JLabel eLabel = new JLabel("*连接异常*", SwingConstants.CENTER);
        JButton eButton = new JButton("重试连接");

        eButton.addActionListener(e -> {
            try {// 重试连接并重新构建窗口
                this.client = new ChatClient(serverAddress, serverPort);
                buildGUI();// 构建窗口UI，监听发送信息动作
                startMessageReceiver();// 启动接收信息的线程
            } catch (IOException ioe) {// 回到异常窗口
                ioe.printStackTrace();
                exceptionUI(serverAddress, serverPort);
            }
        });

        // 上方提示，下方重连按钮
        this.getContentPane().add(eLabel, BorderLayout.CENTER);
        this.getContentPane().add(eButton, BorderLayout.SOUTH);

        this.pack();// 窗口自适应大小
        this.setSize(200, 100);
        this.setLocationRelativeTo(null);// 设置位置（到屏幕中间）
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}