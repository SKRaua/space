package client;

public class StartClient {

    /**
     * 启动客户端
     * 
     * @param args
     */
    public static void main(String[] args) {
        // ("192.168.139.199", 12345);//cn-bj-plc-2.ofalias.net:57275
        // String serverAddress = "26.25.242.82";// localhost
        // 默认地址与端口
        String serverAddress = "localhost";
        String serverPort = "57275";
        // 实例客户端并为其构建UI
        new ChatClient(serverAddress, serverPort);
    }
}