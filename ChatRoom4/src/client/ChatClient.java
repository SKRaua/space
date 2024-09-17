package client;

import javax.swing.JFrame;

/**
 * 客户端管理类
 */
public class ChatClient {

    private static ClientIO clientIO; // 与服务器的IO交互类
    private static JFrame chatClientUI; // 客户端UI
    private static ChatHistoryManager chatHistoryManager; // 聊天记录管理器

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

        // chatClientUI = new ClientGUI(serverAddress, serverPort);
        // chatClientUI = new ExceptionGUI(serverAddress, serverPort);
    }

    /**
     * 获取客户端IO交互类
     * 
     * @return 客户端IO交互类
     */
    public static ClientIO getClientIO() {
        return clientIO;
    }

    /**
     * 设置客户端IO交互类
     * 
     * @param clientIO 客户端IO交互类
     */
    public static void setClientIO(ClientIO clientIO) {
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
