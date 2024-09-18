package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器管理类
 */
public class ChatServer {
    private ServerSocket serverSocket;
    // private ServerGUI serverGUI; // 服务器UI
    private static DBOperations dbOperations; // 数据库连接
    private static ClientManager clientManager; // 客户端管理器

    public ChatServer(int port, String[] dbConfig) {
        try {
            new ServerGUI(dbConfig); // 初始化服务器UI // this.serverGUI =
            ChatServer.dbOperations = new DBOperations(dbConfig);// 实例数据库操作对象
            ChatServer.clientManager = new ClientManager();// 实例客户端管理对象
            this.serverSocket = new ServerSocket(port);// 实例服务器套接字对象
            testConnection();
            Logger.log("聊天室服务端在端口 " + port + " 上运行中。。。");
            startServer();// 等待客户端连接（阻塞程序）
        } catch (IOException e) {
            Logger.log("服务器异常");
            e.printStackTrace();
        }
    }

    /**
     * 启动服务器
     */
    private void startServer() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ServerIO clientHandler = new ServerIO(socket);// , clientManager
                new Thread(clientHandler).start();
                clientManager.addClient(clientHandler);
            } catch (IOException e) {
                Logger.log("与客户端连接异常");
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取数据库操作对象
     * 
     * @return 数据库操作对象
     */
    public static DBOperations getDbOperations() {
        return dbOperations;
    }

    /**
     * 获取客户端管理器
     * 
     * @return 客户端管理器
     */
    public static ClientManager getClientManager() {
        return clientManager;
    }

    /**
     * 尝试重新连接到数据库
     * 
     * @param dbConfig 数据库配置信息
     * @return 连接是否成功
     */
    public static boolean reconnectDatabase(String[] dbConfig) {
        try {
            // 重新实例化数据库操作对象
            dbOperations = new DBOperations(dbConfig);
            Logger.log("数据库重新连接成功！");
            return true;
        } catch (Exception e) {
            Logger.log("数据库重新连接失败！");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 测试数据库连接是否有效
     * 
     * @return 连接是否有效
     */
    public static boolean testConnection() {
        try {
            // 测试连接（具体实现依赖于 DBOperations 的实现）
            Logger.log("测试数据库连接成功！");
            return dbOperations.testConnection();
        } catch (Exception e) {
            Logger.log("测试数据库连接失败！");
            e.printStackTrace();
            return false;
        }
    }

}
