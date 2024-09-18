package client;

import javax.swing.*;
import java.util.List;

import chat.*;
import user.*;
import message.*;

import java.awt.*;
import java.io.IOException;

public class ClientGUI extends JFrame {
    private JTextField inputField;
    private JTextArea chatArea;
    private JButton sendButton;
    private JList<String> chatList; // 聊天选择列表
    private DefaultListModel<String> chatListModel;
    // private ChatHistoryManager chatHistoryManager; // 聊天记录管理器

    private String currentChat; // 当前选择的聊天

    public ClientGUI() {
        super("SKRaua聊天室");
        // chatHistoryManager = new ChatHistoryManager(); // 初始化聊天记录管理器
        buildGUI();
        startMessageReceiver();
        startSyncAsker();
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
        addChat("世界频道");
        switchChat("世界频道");
        // 从聊天记录管理器加载已有的聊天记录
        loadChatHistories();
    }

    /**
     * 加载所有聊天记录并添加到聊天列表
     */
    private void loadChatHistories() {
        chatListModel.addElement("世界频道");
        for (String chatName : ChatClient.getChatHistoryManager().getAllChatHistories().keySet()) {
            chatListModel.addElement(chatName);
        }
        switchChat("世界频道");
    }

    private void addChat(String chatName) {
        if (!chatListModel.contains(chatName)) {
            chatListModel.addElement(chatName);
        }
    }

    /**
     * 切换聊天对象，更新聊天窗口内容
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
        try {
            String message = inputField.getText();
            ChatClient.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // try {
        // String message = inputField.getText();
        // if (currentChat.equals("世界频道")) {
        // // ChatClient.getClientIO().sendMessage(message);
        // ChatClient.getClientIO().sendMessage(new TextMessage(ALLBITS, ABORT,
        // message));
        // } else {
        // ChatClient.getClientIO().sendMessage("/group " + currentChat + " " +
        // message);
        // }
        // inputField.setText("");
        // // chatHistoryManager.appendMessage(currentChat, "我: " + message); // 保存到聊天记录
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    /**
     * 接收消息并分类处理
     */
    private void startMessageReceiver() {
        new Thread(() -> {
            try {
                Message message;
                while ((message = ChatClient.getClientIO().receiveMessage()) != null) {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        if ("chat".equals(textMessage.getMessageType())) {
                            handleChatMessage(textMessage);
                        }
                    } else if (message instanceof SyncChats) {
                        handleSyncChats((SyncChats) message);
                    } else if (message instanceof SyncUsers) {
                        handleSyncUsers((SyncUsers) message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 向服务端发送同步信息请求
     */
    private void startSyncAsker() {
        new Thread(() -> {
            try {
                while (true) {// 向服务端发送同步信息请求
                    TextMessage message = new TextMessage(ChatClient.getUser().getUserID(), 0, "", "syncMessage");
                    ChatClient.getClientIO().sendMessage(message);
                    Thread.sleep(3000);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 处理同步聊天列表的消息
     */
    private void handleSyncChats(SyncChats syncChats) {
        List<Chat> chats = syncChats.getChats();
        for (Chat chat : chats) {
            addChat(chat.getChatName());
            ChatClient.getChatHistoryManager().saveChatHistory(chat);
        }
    }

    /**
     * 处理同步聊天列表的消息
     */
    private void handleSyncUsers(SyncUsers syncUsers) {
        User user = syncUsers.getUser();
        ChatClient.setUser(user);// 获取本地用户信息
        List<User> users = syncUsers.getUsers();
        ChatClient.getUsersCache().addCache(users);// 获取其他用户信息
    }

    /**
     * 处理聊天消息
     */
    private void handleChatMessage(TextMessage textMessage) {
        String message = ChatClient.getUsersCache().getUsername(textMessage.getSenderId()) + ": "
                + textMessage.getContent() + "\n";
        if (currentChat != null
                && currentChat.equals(ChatClient.getChatHistoryManager().getChatname(textMessage.getChatId()))) {
            chatArea.append(message);
        }
        ChatClient.getChatHistoryManager().appendMessage(currentChat, message);
    }

    // /**
    // * 接收消息并分类处理
    // */
    // private void startMessageReceiver() {
    // new Thread(() -> {
    // try {
    // Message message;
    // while ((message = ChatClient.getClientIO().receiveMessage()) != null) {
    // if (message instanceof TextMessage) {
    // TextMessage textMessage = (TextMessage) message;
    // switch (textMessage.getMessageType()) {
    // case "sync":

    // break;
    // case "chat":// 群组或私聊

    // break;

    // default:

    // break;
    // }
    // } else if (message instanceof FileMessage) {
    // // 处理文件信息
    // // ChatServer.getClientManager().broadcastMessageToAll(message);
    // }
    // }
    // } catch (IOException | ClassNotFoundException e) {
    // e.printStackTrace();
    // }
    // }).start();
    // }
}
