package server;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * 聊天室服务端,负责与客户端建立连接
 */
public class WaitLink {
    protected static final int PORT = 12345;// 连接端口
    protected static Set<clientHandler> clientHandlers;// 客户机处理线程集

    /**
     * 实例服务端
     * 
     * @param url       服务端连接的数据库地址（mysql）
     * @param user      数据库用户
     * @param passsword 数据库密码
     */
    public WaitLink() {
        clientHandlers = new HashSet<>();

        buildConnection();// 等待客户端连接（会在等待连接中阻塞程序）
    }

    /**
     * 尝试与新的客户端连接
     */
    private void buildConnection() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Logger.log("Chat server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();// 等待客户端连接（阻塞循环）
                clientHandler clientHandler = new clientHandler(clientSocket, clientHandlers);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();// 实例新的客户端处理线程，分发数据处理任务
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求在线用户数与用户列表（包括未登陆用户）
     */
    public List<String> clientListMessage() throws IOException {
        List<String> clientList = new ArrayList<>();
        for (clientHandler clientHandler : clientHandlers) {
            String username = clientHandler.getUsername();
            if (username != null) {
                clientList.add(username);
            }
        }
        return clientList;
    }

}
