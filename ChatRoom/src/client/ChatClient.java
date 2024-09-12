package client;

import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatClient(String serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void sendFile(File file) throws IOException {
        // 假设服务器有专门处理文件传输的功能
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
        out.writeUTF("FILE_TRANSFER:" + file.getName()); // 先发送文件传输命令和文件名
        out.writeInt(fileBytes.length); // 发送文件长度
        out.write(fileBytes); // 发送文件内容
        out.flush();
    }

    // public void sendFile(java.io.File file) throws IOException {
    //     // 打开文件输入流
    //     try (FileInputStream fileInputStream = new FileInputStream(file)) {
    //         // 通过输出流发送文件
    //         OutputStream outputStream = socket.getOutputStream();
    //         byte[] buffer = new byte[4096];
    //         int bytesRead;
    
    //         // 发送文件名
    //         PrintWriter writer = new PrintWriter(outputStream, true);
    //         writer.println("FILE:" + file.getName());
    
    //         // 发送文件内容
    //         while ((bytesRead = fileInputStream.read(buffer)) != -1) {
    //             outputStream.write(buffer, 0, bytesRead);
    //         }
    
    //         outputStream.flush();
    //     }
    //}
    

}
