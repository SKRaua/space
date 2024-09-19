package server;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 服务端的控制终端UI
 */
public class ServerGUI extends JFrame {
    // UI组件
    private JTextField urlField;// 数据库地址
    private JTextField userField;// 数据库用户名
    private JPasswordField passwordField; // 密码框
    private JTextField terminalField;// 终端区域
    private JTextField sqlField;// sql查询框
    private JTextArea logArea;// 日志框

    private JButton connectButton;// 连接数据库按钮
    private JButton testConnectionButton;// 测试连接按钮
    private JButton executeSqlButton;// 执行sql按钮
    private JButton executeCommandButton;// 执行指令按钮

    public ServerGUI(String[] dbConfig) {
        super("SKRaua聊天室服务终端");
        buildGUI(dbConfig);
    }

    /**
     * 构建UI
     * 
     * @param dbConfig
     */
    private void buildGUI(String[] dbConfig) {
        // 初始化UI组件
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        Logger.setLogArea(logArea);// 处理日志记录

        urlField = new JTextField(dbConfig[0]);
        userField = new JTextField(dbConfig[1]);
        passwordField = new JPasswordField(dbConfig[2]);
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

    /**
     * 重连数据库方法
     */
    private void reconnectDatabase() {
        String urlLink = urlField.getText();
        String userLink = userField.getText();
        String passwordLink = new String(passwordField.getPassword());
        String[] dbConfig = { urlLink, userLink, passwordLink };
        boolean isConnected = ChatServer.getDbOperations().reconnectDatabase(dbConfig);
        if (isConnected) {
            Logger.log("数据库重连成功");
        } else {
            Logger.log("数据库重连失败\n");
        }
    }

    /**
     * 测试数据库连接
     */
    private void testConnection() {
        boolean isConnected = ChatServer.getDbOperations().testConnection();
        if (isConnected) {
            Logger.log("数据库连接测试成功");
        } else {
            Logger.log("数据库连接测试失败");
        }
    }

    /**
     * 执行sql语句
     */
    private void executeSqlStatement() {
        try {
            String sql = sqlField.getText();
            if (!sql.isEmpty()) {
                if (sql.trim().toLowerCase().startsWith("select")) {// 查询
                    List<Map<String, Object>> result = ChatServer.getDbOperations().executeQuery(sql);
                    Logger.log("SQL 查询结果: ");
                    for (Map<String, Object> row : result) {
                        Logger.log(row.toString());
                    }
                } else {// 其他操作
                    int rowsAffected = ChatServer.getDbOperations().executeUpdate(sql);
                    Logger.log(" 执行SQL 影响行数: " + rowsAffected);
                }
            } else {
                Logger.log("SQL 不能为空");
            }
        } catch (SQLException e) {
            Logger.log("数据库操作异常");

        }
    }

    /**
     * 终端指令执行
     */
    private void executeTerminalCommand() {
        String command = terminalField.getText();
        if (!command.isEmpty()) {
            if (command.startsWith("/")) {
                Logger.log("执行终端指令: " + command);
                String[] commandInfo = command.substring(1).split(" ", 2);
                if (commandInfo.length > 0) {
                    String commandType = commandInfo[0];
                    switch (commandType) {
                        case "help":// 获取帮助
                            Logger.log("[/kickout <用户名>] 踢出用户");
                            Logger.log("[/admemtochat <用户名> <聊天>] 把用户加入聊天");
                            Logger.log("[/rmmemfromchat <用户名> <聊天>] 把用户移除聊天");
                            Logger.log("[/createchat <聊天>] 创建聊天");
                            Logger.log("[/restart <端口>] 在指定端口上重建服务器连接");
                            break;
                        case "kickout":
                            if (commandInfo.length == 2) {
                                String kickedUser = commandInfo[1].trim();
                                if (ChatServer.getClientManager().removeClient(kickedUser)) {
                                    Logger.log("成功踢出：" + kickedUser);
                                } else {
                                    Logger.log("异常或 " + kickedUser + " 不在线");
                                }
                            } else {
                                Logger.log("格式错误：/kickOut <用户名>");
                            }
                            break;
                        case "admemtochat":
                            if (commandInfo.length == 2) {
                                String[] memToChat = commandInfo[1].split(" ", 2);
                                if (memToChat.length == 2) {
                                    String addedUser = memToChat[0].trim();
                                    String chatName = memToChat[1].trim();
                                    if (ChatServer.getClientManager().addMemberToChat(addedUser, chatName)) {
                                        Logger.log("用户 " + addedUser + " 成功加入聊天 " + chatName);
                                    } else {
                                        Logger.log("添加用户失败或聊天不存在");
                                    }
                                } else {
                                    Logger.log("格式错误：/addMemToChat <用户名> <聊天>");
                                }
                            } else {
                                Logger.log("格式错误：/addMemToChat <用户名> <聊天>");
                            }
                            break;
                        case "rmmemfromchat":
                            if (commandInfo.length == 2) {
                                String[] removeInfo = commandInfo[1].split(" ", 2);
                                if (removeInfo.length == 2) {
                                    String removedUser = removeInfo[0].trim();
                                    String chatName = removeInfo[1].trim();
                                    if (ChatServer.getClientManager().removeMemberFromChat(removedUser, chatName)) {
                                        Logger.log("用户 " + removedUser + " 成功从聊天 " + chatName + " 中移除");
                                    } else {
                                        Logger.log("移除用户失败或聊天不存在");
                                    }
                                } else {
                                    Logger.log("格式错误：/removeMemberFromChat <用户名> <聊天>");
                                }
                            } else {
                                Logger.log("格式错误：/removeMemberFromChat <用户名> <聊天>");
                            }
                            break;
                        case "createchat":
                            if (commandInfo.length == 2) {
                                String chatName = commandInfo[1].trim();
                                if (ChatServer.getClientManager().createChat(chatName)) {
                                    Logger.log("群聊 " + chatName + " 创建成功");
                                } else {
                                    Logger.log("群聊 " + chatName + " 创建异常");
                                }
                            } else {
                                Logger.log("格式错误：/createChat <聊天名称>");
                            }
                            break;
                        case "restart":
                            if (commandInfo.length == 2) {
                                int port = Integer.parseInt(commandInfo[1].trim());
                                if (ChatServer.reStartServer(port)) {
                                    Logger.log("聊天室服务端在端口 " + port + " 上运行中。。。");
                                } else {
                                    Logger.log("服务器创建失败");
                                }
                            } else {
                                Logger.log("格式错误：/createChat <聊天名称>");
                            }
                            break;
                        default:
                            Logger.log("检查指令格式");
                            break;
                    }
                }
            }
        } else {
            Logger.log("终端指令不能为空");
        }
    }
}
