package client;

import java.io.IOException;

import javax.swing.JFrame;

import message.*;
import user.*;

/**
 * 客户端管理类
 */
public class ChatClient {

    private static MessageIO clientIO; // 与服务器的IO交互类
    private static JFrame chatClientUI; // 客户端UI
    private static ChatHistoryManager chatHistoryManager; // 聊天记录管理器
    private static User user;// 用户

    public ChatClient(String serverAddress, int serverPort) {
        chatHistoryManager = new ChatHistoryManager(); // 初始化聊天记录管理器
        initializeUI(serverAddress, serverPort); // 初始化UI
    }

    /**
     * 初始化客户端UI
     * 
     * @param serverAddress 服务器地址
     * @param serverPort    服务器端口
     */
    private void initializeUI(String serverAddress, int serverPort) {
        chatClientUI = new SelectServerGUI(); // 创建选择服务器的界面
        user.setUserID(-1);// 未登录用户
        // chatClientUI = new ClientGUI(serverAddress, serverPort);
        // chatClientUI = new ExceptionGUI(serverAddress, serverPort);
    }

    /**
     * 发送信息
     * 
     * @throws IOException
     */
    public static void sendMessage(String textMessage) throws IOException {
        clientIO.sendMessage(new TextMessage(-1, 0, textMessage));
        // try {
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

                // String message;
                // while ((message = ChatClient.getClientIO().receiveMessage()) != null) {
                // if (message.startsWith("/group")) {
                // String[] parts = message.split(" ", 3);
                // if (parts.length == 3) {
                // String groupName = parts[1];
                // String groupMessage = parts[2];
                // ChatClient.getChatHistoryManager().appendMessage(groupName, groupMessage); //
                // 保存群聊记录
                // if (groupName.equals(currentChat)) {
                // chatArea.append(message + "\n"); // 只更新当前聊天窗口的内容
                // }
                // }
                // } else {
                // ChatClient.getChatHistoryManager().appendMessage("世界频道", message); //
                // 保存公共频道记录
                // if (currentChat.equals("世界频道")) {
                // chatArea.append(message + "\n"); // 只更新当前聊天窗口的内容
                // }
                // }
                // }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}

    public int getUserID() {
        return user.getUserID();
    }

    public String getUserName() {
        return user.getUserName();
    }

    /**
     * 获取客户端IO交互类
     * 
     * @return 客户端IO交互类
     */
    public static MessageIO getClientIO() {
        return clientIO;
    }

    /**
     * 设置客户端IO交互类
     * 
     * @param clientIO 客户端IO交互类
     */
    public static void setClientIO(MessageIO clientIO) {
        ChatClient.clientIO = clientIO;
    }

    /**
     * 获取客户端UI
     * 
     * @return 客户端UI
     */
    public static JFrame getChatClientUI() {
        return chatClientUI;
    }

    /**
     * 设置客户端UI
     * 
     * @param chatClientUI 客户端UI
     */
    public static void setChatClientUI(JFrame chatClientUI) {
        ChatClient.chatClientUI = chatClientUI;
    }

    /**
     * 获取聊天记录管理器
     * 
     * @return 聊天记录管理器
     */
    public static ChatHistoryManager getChatHistoryManager() {
        return chatHistoryManager;
    }

    /**
     * 设置聊天记录管理器
     * 
     * @param chatHistoryManager 聊天记录管理器
     */
    public static void setChatHistoryManager(ChatHistoryManager chatHistoryManager) {
        ChatClient.chatHistoryManager = chatHistoryManager;
    }
}
