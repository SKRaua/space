package message;

import java.io.*;
import java.net.*;

/**
 * 聊天室客户端
 */
public class MessageIO {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * 实例客户端
     * 
     * @param address 连接地址
     * @param port    端口
     * @throws IOException
     */
    public MessageIO(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * 实例客户端
     * 
     * @param socket 套接字
     * @throws IOException
     */
    public MessageIO(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * 发送信息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    // /**
    //  * 发送文本信息
    //  *
    //  * @param message
    //  * @throws IOException
    //  */
    // public void sendMessage(String message) throws IOException {
    //     out.writeObject(new TextMessage("0", "","",message));
    //     out.flush();
    // }

    /**
     * 读取信息
     *
     * @return
     * @throws IOException
     */
    public Message receiveMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    /**
     * 关闭连接
     *
     * @return
     * @throws IOException
     */
    public void close() throws IOException {
        this.socket.close();
        this.in.close();
        this.out.close();
    }
}
