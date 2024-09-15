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
    protected static DBOperations dbOperations; // 数据库连接
    protected static ClientManager clientManager; // 客户端管理器

    public ChatServer(int port, String[] dbConfig) {
        try {
            new ServerGUI(dbConfig); // 初始化服务器UI // this.serverGUI =
            ChatServer.dbOperations = new DBOperations(dbConfig);// 实例数据库操作对象
            ChatServer.clientManager = new ClientManager();// 实例客户端管理对象
            this.serverSocket = new ServerSocket(port);// 实例服务器套接字对象
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
        Logger.log("Chat server is running...");
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

}
