package server;

import javax.swing.*;
import java.util.*;

public class ServerGUI extends JFrame {
    private WaitLink linkWaiter; // 网络服务端，处理客户端连接
    protected static DBConnection dbConnection; // 数据库连接

    // UI组件
    private JTextField urlField;
    private JTextField userField;
    private JPasswordField passwordField; // 使用JPasswordField代替JTextField
    private JTextField terminalField;
    private JTextField sqlField;
    private JTextArea logArea;

    private JButton connectButton;
    private JButton testConnectionButton;
    private JButton executeSqlButton;
    private JButton executeCommandButton;

    public ServerGUI(String url, String user, String password) {
        super("SKRaua聊天室服务终端");
        ServerGUI.dbConnection = DBConnection.getInstance(url, user, password);
        buildGUI(url, user, password);
        this.linkWaiter = new WaitLink(); // 实例化 ChatServer
    }

    private void buildGUI(String url, String user, String password) {
        // 初始化UI组件
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        Logger.setLogArea(logArea);// 处理日志记录

        urlField = new JTextField(url);
        userField = new JTextField(user);
        passwordField = new JPasswordField(password);
        terminalField = new JTextField();
        sqlField = new JTextField("SELECT * FROM users");

        connectButton = new JButton("重连数据库");
        testConnectionButton = new JButton("测试数据库连接");
        executeSqlButton = new JButton("执行SQL");
        executeCommandButton = new JButton("执行终端指令");

        // 设置按钮的动作
        connectButton.addActionListener(e -> reconnectDatabase());
        testConnectionButton.addActionListener(e -> testConnection());
        executeSqlButton.addActionListener(e -> executeSqlStatement());
        executeCommandButton.addActionListener(e -> executeTerminalCommand());

        // 设置布局
        setLayout(null); // 使用绝对布局

        // 添加数据库设置区块
        JLabel urlLabel = new JLabel("SQL地址:");
        urlLabel.setBounds(10, 10, 100, 25);
        urlField.setBounds(80, 10, 320, 25);
        JLabel userLabel = new JLabel("SQL用户名:");
        userLabel.setBounds(10, 40, 100, 25);
        userField.setBounds(80, 40, 320, 25);
        JLabel passwordLabel = new JLabel("SQL密码:");
        passwordLabel.setBounds(10, 70, 100, 25);
        passwordField.setBounds(80, 70, 320, 25);
        connectButton.setBounds(420, 10, 150, 25);
        testConnectionButton.setBounds(420, 40, 150, 25);

        // 添加日志区域
        logScrollPane.setBounds(10, 110, 570, 200); // 日志区域的位置和大小

        // 添加SQL查询和终端指令区块
        JLabel sqlLabel = new JLabel("SQL查询:");
        sqlLabel.setBounds(10, 320, 100, 25);
        sqlField.setBounds(80, 320, 320, 25);
        executeSqlButton.setBounds(420, 320, 150, 25);

        JLabel terminalLabel = new JLabel("终端指令:");
        terminalLabel.setBounds(10, 360, 100, 25);
        terminalField.setBounds(80, 360, 320, 25);
        executeCommandButton.setBounds(420, 360, 150, 25);

        // 添加组件到窗口
        this.getContentPane().add(urlLabel);
        this.getContentPane().add(urlField);
        this.getContentPane().add(userLabel);
        this.getContentPane().add(userField);
        this.getContentPane().add(passwordLabel);
        this.getContentPane().add(passwordField);
        this.getContentPane().add(connectButton);
        this.getContentPane().add(testConnectionButton);
        this.getContentPane().add(logScrollPane);
        this.getContentPane().add(sqlLabel);
        this.getContentPane().add(sqlField);
        this.getContentPane().add(executeSqlButton);
        this.getContentPane().add(terminalLabel);
        this.getContentPane().add(terminalField);
        this.getContentPane().add(executeCommandButton);

        // 设置窗口属性
        this.setSize(600, 450); // 调整窗口大小以适应所有组件
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false); // 设置窗口不可缩放
        this.setVisible(true);
    }

    private void reconnectDatabase() {
        String urlLink = urlField.getText();
        String userLink = userField.getText();
        String passwordLink = new String(passwordField.getPassword());
        ServerGUI.dbConnection = DBConnection.getInstance(urlLink, userLink, passwordLink);
        if (dbConnection.testConnection()) {
            logArea.append("数据库重连成功\n");
        } else {
            logArea.append("数据库重连失败\n");
        }
    }

    private void testConnection() {
        boolean isConnected = dbConnection.testConnection();
        if (isConnected) {
            logArea.append("数据库连接测试成功\n");
        } else {
            logArea.append("数据库连接测试失败\n");
        }
    }

    private void executeSqlStatement() {
        String sql = sqlField.getText();
        if (!sql.isEmpty()) {
            if (sql.trim().toLowerCase().startsWith("select")) {
                java.util.List<Map<String, Object>> result = dbConnection.executeQuery(sql);
                logArea.append("SQL 查询结果: \n");
                for (Map<String, Object> row : result) {
                    logArea.append(row.toString() + "\n");
                }
            } else {
                int rowsAffected = dbConnection.executeUpdate(sql);
                logArea.append("SQL 执行成功，影响行数: " + rowsAffected + "\n");
            }
        } else {
            logArea.append("SQL 不能为空\n");
        }
    }

    private void executeTerminalCommand() {
        String command = terminalField.getText();
        if (!command.isEmpty()) {
            logArea.append("执行终端指令: " + command + "\n");
            terminalField.setText("");
        } else {
            logArea.append("终端指令不能为空\n");
        }
    }
}
