package client;

import java.io.IOException;

/**
 * 服务器管理类
 */
public class ChatServer {

    private ClientIO clientIO;

    public ChatServer(String serverAddress, int serverPort) {
        try {
            this.clientIO = new ClientIO(serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ClientGUI(serverAddress, serverPort);

    }

}
