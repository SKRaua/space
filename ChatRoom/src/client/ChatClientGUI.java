package client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ChatClientGUI {
    private ChatClient client;
    private JFrame frame;
    private JTextField inputField;
    private JTextArea chatArea;

    public ChatClientGUI(ChatClient client) {
        this.client = client;
        buildGUI();// 构建窗口ui，监听发送信息动作
        startMessageReceiver();// 启动接收信息的线程
    }

    /**
     * 配置客户端UI，监听发送信息的动作
     */
    private void buildGUI() {
        // 聊天室，输入框，聊天区域，发送按钮
        frame = new JFrame("聊天室");
        inputField = new JTextField(40);
        chatArea = new JTextArea(20, 40);
        chatArea.setEditable(false);
        JButton sendButton = new JButton("发送");
        //
        JButton sendFileButton = new JButton("发送文件");

        // 为输入框和发送按钮添加监听
        inputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        sendFileButton.addActionListener(e -> sendFile());

        // 边缘布局，上方聊天区域（使用滚动面板），下方输入框和发送按钮
        frame.getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);
        //
        panel.add(sendFileButton);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        frame.pack();// 窗口自适应大小
        frame.setLocationRelativeTo(null);// 设置位置（到屏幕中间）
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * 发送信息
     */
    private void sendMessage() {
        String message = inputField.getText();
        client.sendMessage(message);
        inputField.setText("");
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
     * 发送文件
     */
    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            // 获取选择的文件
            java.io.File file = fileChooser.getSelectedFile();
            try {
                client.sendFile(file); // 调用客户端的发送文件方法
                chatArea.append("文件已发送: " + file.getName() + "\n");
            } catch (IOException e) {
                chatArea.append("发送文件失败: " + file.getName() + "\n");
                e.printStackTrace();
            }
        }
    }

}
