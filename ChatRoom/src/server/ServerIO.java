package server;

import java.io.*;
import java.net.*;

import message.*;

public class ServerIO {
    private ObjectOutputStream out; // 向客户端输出
    private ObjectInputStream in; // 从客户端输入
    private Socket socket; // 连接套接字

    public ServerIO(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * 发送信息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    /**
     * 读取信息
     *
     * @return
     * @throws IOException
     */
    public Message receiveMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

}
