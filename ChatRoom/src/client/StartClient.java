package client;

public class StartClient {

    /**
     * 启动客户端
     * 
     * @param args
     */
    public static void main(String[] args) {
        // ("192.168.139.199", 12345);
        String serverAddress = "localhost";
        int serverPort = 12345;
        // 实例客户端并为其构建UI
            new ClientGUI(serverAddress, serverPort);
    }
}
