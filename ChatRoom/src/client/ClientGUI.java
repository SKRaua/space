package client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端UI窗口类
 */
public class ClientGUI extends JFrame {
    private ClientIO client;
    private JTextField inputField;
    private JTextArea chatArea;
    private JButton sendButton;
    private JList<String> chatList; // 聊天选择列表
    private DefaultListModel<String> chatListModel;
    private Map<String, StringBuilder> chatHistoryMap; // 存储每个聊天对象的聊天历史
    private String currentChat; // 当前选择的聊天对象

    public ClientGUI(String serverAddress, int serverPort) {
        super("SKRaua聊天室");
        try {
            this.client = new ClientIO(serverAddress, serverPort);
            chatHistoryMap = new HashMap<>(); // 初始化聊天历史的存储
            buildGUI(); // 构建窗口UI，监听发送信息动作
            startMessageReceiver(); // 启动接收信息的线程
        } catch (IOException e) {
            e.printStackTrace();
            new ExceptionGUI(serverAddress, serverPort); // 构建连接异常时的UI
        }
    }

    /**
     * 配置客户端UI，监听发送信息的动作
     */
    private void buildGUI() {
        // 创建聊天对象选择列表
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.addListSelectionListener(e -> switchChat(chatList.getSelectedValue()));

        // 聊天区和输入区
        inputField = new JTextField(30);
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        sendButton = new JButton("发送");

        // 为输入框和发送按钮添加监听用于发送信息
        inputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        // 设置主窗口的布局
        this.getContentPane().setLayout(new BorderLayout());

        // 左侧聊天对象列表，右侧为聊天区域和输入区，可滚动容器
        this.getContentPane().add(new JScrollPane(chatList), BorderLayout.WEST);
        this.getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // 输入区和发送按钮放置在窗口底部
        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);
        this.getContentPane().add(panel, BorderLayout.SOUTH);

        this.pack(); // 窗口自适应大小
        this.setLocationRelativeTo(null); // 设置位置（到屏幕中间）
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        // 添加默认会话
        addChat("世界频道"); // 默认加入一个公共聊天
        switchChat("世界频道");
    }

    /**
     * 添加新的聊天对象或群聊
     */
    private void addChat(String chatName) {
        if (!chatHistoryMap.containsKey(chatName)) {
            chatHistoryMap.put(chatName, new StringBuilder());
            chatListModel.addElement(chatName);
        }
    }

    /**
     * 删除群聊
     */
    private void deleteChat(String chatName) {
        if (chatHistoryMap.containsKey(chatName)) {
            chatHistoryMap.remove(chatName);
            chatListModel.removeElement(chatName);
        }
    }

    /**
     * 切换聊天对象，更新聊天窗口内容
     */
    private void switchChat(String chatName) {
        if (chatName != null && !chatName.equals(currentChat)) {
            currentChat = chatName;
            chatArea.setText(chatHistoryMap.get(chatName).toString()); // 更新聊天区域内容
        }
    }

    // /**
    // * 发送信息
    // */
    // private void sendMessage() {
    // try {
    // String message = inputField.getText();
    // client.sendMessage(message);
    // inputField.setText("");
    // appendMessage(currentChat, "我: " + message);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    /**
     * 发送信息
     */
    private void sendMessage() {
        try {
            String message = inputField.getText();
            if (currentChat.equals("世界频道")) {
                client.sendMessage(message); // 如果是公共频道，不加前缀
            } else {
                client.sendMessage("/group " + currentChat + " " + message); // 群聊消息加上前缀
            }
            inputField.setText(""); // 清空输入框
            // appendMessage(currentChat, "我: " + message); // 更新当前聊天窗口
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收消息并分类处理
     */
    private void startMessageReceiver() {
        new Thread(() -> {
            try {
                String message;
                while ((message = client.receiveMessage()) != null) {
                    // 简单判断消息格式（群聊或私聊）
                    if (message.startsWith("/group")) {
                        String[] parts = message.split(" ", 3);
                        if (parts.length == 3) {
                            String groupName = parts[1];
                            String groupMessage = parts[2];
                            addChat(groupName); // 添加新的群聊
                            appendMessage(groupName, groupMessage);
                        }
                    } else if (message.startsWith("/leaveGroup")) {
                        String[] parts = message.split(" ", 2);
                        if (parts.length == 2) {
                            String groupName = parts[1];
                            deleteChat(groupName);// 删除群聊
                        }
                    } else {
                        appendMessage("世界频道", message); // 默认处理为公共聊天
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 添加消息到指定聊天对象的聊天历史中
     */
    private void appendMessage(String chatName, String message) {
        chatHistoryMap.get(chatName).append(message).append("\n");
        if (chatName.equals(currentChat)) {
            chatArea.append(message + "\n"); // 只更新当前聊天窗口的内容
        }
    }
}
