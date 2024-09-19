package client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * 聊天室客户端的主UI
 */
public class ClientGUI extends JFrame {
    private JTextField inputField;// 输入框
    private JTextArea chatArea;// 聊天框
    private JButton sendButton;// 发送按钮
    private JList<String> chatList; // 聊天选择列表
    private DefaultListModel<String> chatListModel;// 聊天列表内容
    private String currentChat; // 当前选择的聊天

    public ClientGUI() {
        super("SKRaua聊天室");
        // chatHistoryManager = new ChatHistoryManager(); // 初始化聊天记录管理器
        buildGUI();
        startMessageReceiver();
    }

    /**
     * 配置客户端UI，监听发送信息的动作
     */
    private void buildGUI() {
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.addListSelectionListener(e -> switchChat(chatList.getSelectedValue()));

        inputField = new JTextField(30);
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        sendButton = new JButton("发送");

        inputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(new JScrollPane(chatList), BorderLayout.WEST);
        this.getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);
        this.getContentPane().add(panel, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        // 添加默认会话
        // addChat("世界频道");
        // switchChat("世界频道");
        // 从聊天记录管理器加载已有的聊天记录
        loadChatHistories();
    }

    /**
     * 加载所有聊天记录并添加到聊天列表
     */
    private void loadChatHistories() {
        addChat("世界频道");
        for (String chatName : ChatClient.getChatHistoryManager().getAllChatHistories().keySet()) {
            addChat(chatName);
        }
        switchChat("世界频道");
    }

    /**
     * 添加一个聊天到聊天列表
     * 
     * @param chatName 聊天名
     */
    private void addChat(String chatName) {
        if (!chatListModel.contains(chatName)) {// 防止重复
            chatListModel.addElement(chatName);
        }
    }

    /**
     * 切换聊天，更新聊天窗口内容
     * 
     * @param chatName 切换到的聊天名
     */
    private void switchChat(String chatName) {
        if (chatName != null && !chatName.equals(currentChat)) {
            currentChat = chatName;
            chatArea.setText(ChatClient.getChatHistoryManager().getChatHistory(chatName)); // 更新聊天区域内容
        }
    }

    /**
     * 发送信息
     */
    private void sendMessage() {
        try {// 依据聊天的不同，发送不同的指令信息
            String message = inputField.getText();
            if (currentChat.equals("世界频道")) {
                ChatClient.getClientIO().sendMessage(message);
            } else {
                ChatClient.getClientIO().sendMessage("/chat " + currentChat + " " + message);
            }
            inputField.setText("");
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
                while ((message = ChatClient.getClientIO().receiveMessage()) != null) {
                    if (message.startsWith("/chat")) {// 聊天信息，匹配聊天框
                        String[] parts = message.split(" ", 3);
                        if (parts.length == 3) {
                            String chatName = parts[1];
                            String chatMessage = parts[2];
                            ChatClient.getChatHistoryManager().appendMessage(chatName, chatMessage); // 保存群聊记录
                            addChat(chatName);
                            if (chatName.equals(currentChat)) {
                                chatArea.append(chatMessage + "\n"); // 只更新当前聊天窗口的内容
                            }
                        }
                    } else if (message.startsWith("/rmChat")) {// 移出群聊
                        String[] parts = message.split(" ", 2);
                        if (parts.length == 2) {
                            String chatNameToRm = parts[1];
                            ChatClient.getChatHistoryManager().deleteChatHistory(chatNameToRm);
                            chatListModel.removeElement(chatNameToRm);
                        }
                    } else if (message.startsWith("/login")) {//登陆信息
                        String[] parts = message.split(" ", 2);
                        if (parts.length == 2) {
                            String username = parts[1];
                            ChatClient.setUsername(username);
                            ChatClient.getChatHistoryManager().loadChatHistory(ChatClient.getUsername());
                        }
                    } else {
                        ChatClient.getChatHistoryManager().appendMessage("世界频道", message); // 保存公共频道记录
                        if (currentChat.equals("世界频道")) {
                            chatArea.append(message + "\n"); // 只更新当前聊天窗口的内容
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
