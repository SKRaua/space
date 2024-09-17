package client;

import java.io.*;
import java.net.*;

/**
 * 聊天室客户端
 */
public class ClientIO {
    private Socket socket;// 连接套接字
    private BufferedReader in;// 从服务端读取
    private PrintWriter out;// 向服务端发出

    /**
     * 实例客户端
     * 
     * @param serverAddress 服务端地址
     * @param serverPort    端口
     * @throws IOException
     */
    public ClientIO(String serverAddress, int serverPort) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * 发送信息
     * 
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    /**
     * 读取信息
     * 
     * @return
     * @throws IOException
     */
    public String receiveMessage() throws IOException {
        return in.readLine();
    }

}
