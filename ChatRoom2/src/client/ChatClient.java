package client;

import java.io.*;
import java.net.*;

public class ChatClient {
    private String username;
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;

    public ChatClient(String serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataInputStream in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        // out.println(message);
        out.writeUTF(message); // 使用 writeUTF 发送文本消息
        out.flush();
    }

    public String receiveMessage() throws IOException {
        return in.readUTF();
        // return in.readLine();
    }

    public void sendFile(File file) throws IOException {
        // 假设服务器有专门处理文件传输的功能
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
        out.writeUTF("FILE_TRANSFER:" + file.getName()); // 先发送文件传输命令和文件名
        out.writeInt(fileBytes.length); // 发送文件长度
        out.write(fileBytes); // 发送文件内容
        out.flush();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void closeLink() throws IOException {
        this.socket.close();
        this.in.close();
        this.out.close();
    }

    public void reLink() throws IOException {
        this.closeLink();
        // this.socket = new Socket(this.socket.getInetAddress(),
        // this.socket.getPort());
        this.socket = new Socket("localhost", 12345);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }
}
