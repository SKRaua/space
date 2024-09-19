package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器管理类
 */
public class ChatServer {
    private static ServerSocket serverSocket;
    // private ServerGUI serverGUI; // 服务器UI
    private static DBOperations dbOperations; // 数据库操作类
    private static ClientManager clientManager; // 在线客户端线程管理器
    private static Thread server;// 等待客户端连接的线程

    public ChatServer(int port, String[] dbConfig) {
        try {
            new ServerGUI(dbConfig); // 初始化服务器UI // this.serverGUI =
            ChatServer.dbOperations = new DBOperations(dbConfig);
            ChatServer.clientManager = new ClientManager();
            ChatServer.serverSocket = new ServerSocket(port);
            testConnection();
            Logger.log("聊天室服务端在端口 " + port + " 上运行中。。。");
            Logger.log("执行终端指令: /help 查看帮助\n");

            ChatServer.server = startServer();// 启动线程等待客户端连接
        } catch (IOException e) {
            Logger.log("服务器异常");
            e.printStackTrace();
        }
    }

    /**
     * 启动服务器，启动新线程用于等待连接
     */
    private static Thread startServer() {
        Thread server = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket);
                    Thread handlerThread = new Thread(clientHandler);
                    handlerThread.start();
                    clientManager.addClient(clientHandler, handlerThread);
                } catch (IOException e) {
                    Logger.log("与客户端连接异常");
                    e.printStackTrace();
                }
            }
        });
        server.start();
        return server;
    }

    /**
     * 重启服务器
     * 
     * @param port 使用的端口
     * @return 重启是否成功
     */
    public static boolean reStartServer(int port) {
        server.interrupt();// 停止等待连接
        clientManager.removeAll();// 清理客户端处理线程
        try {
            ChatServer.serverSocket.close();
            ChatServer.serverSocket = new ServerSocket(port);
            ChatServer.server = startServer();// 启动线程等待客户端连接
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
