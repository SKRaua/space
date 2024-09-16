package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SelectServerGUI extends JFrame {

    private JTextField addressField;
    private JTextField portField;
    private JLabel statusLabel;

    public SelectServerGUI() {
        super("去哪聊~~");

        // 设置窗口大小、布局方式和关闭操作
        setSize(400, 220);
        setLayout(null); // 绝对布局
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // 设置位置到屏幕中间
        setResizable(false); // 设置窗口不可调大小

        // 创建组件
        JLabel addressLabel = new JLabel("服务器地址: ");
        addressLabel.setBounds(50, 30, 80, 30);

        addressField = new JTextField(15);
        addressField.setBounds(140, 30, 200, 30);

        JLabel portLabel = new JLabel("端口: ");
        portLabel.setBounds(50, 70, 80, 30);

        portField = new JTextField(5);
        portField.setBounds(140, 70, 200, 30);

        JButton connectButton = new JButton("连接");
        connectButton.setBounds(150, 120, 100, 30);

        statusLabel = new JLabel("");
        statusLabel.setBounds(50, 150, 300, 30);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER); // 水平居中
        statusLabel.setForeground(Color.RED); // 设置红色文本

        // 添加组件到窗口
        add(addressLabel);
        add(addressField);
        add(portLabel);
        add(portField);
        add(connectButton);
        add(statusLabel);

        // 设置连接按钮监听事件
        connectButton.addActionListener(new ConnectButtonListener());
        addressField.addActionListener(new ConnectButtonListener());
        portField.addActionListener(new ConnectButtonListener());

        // 设置窗口可见
        setVisible(true);
    }

    /**
     * 按钮点击后的事件监听器类
     */
    private class ConnectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String serverAddress = addressField.getText();
            int serverPort;

            // 处理端口号输入错误
            try {
                serverPort = Integer.parseInt(portField.getText());
            } catch (NumberFormatException ex) {
                statusLabel.setText("端口号格式不正确！");
                return;
            }

            // 尝试连接服务器
            try {
                ChatClient.setClientIO(new ClientIO(serverAddress, serverPort));
                // ChatClient.clientIO = new ClientIO(serverAddress, serverPort);
                statusLabel.setText("连接成功！");
                // 如果连接成功，跳转到主界面
                new ClientGUI();
                dispose(); // 关闭当前窗口
            } catch (IOException ex) {
                statusLabel.setText("连接失败，请检查地址和端口！");
            }
        }
    }
}
