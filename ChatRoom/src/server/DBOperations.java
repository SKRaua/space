package server;

import java.sql.*;
import java.util.*;

import chat.Chat;
import message.*;

import java.time.ZonedDateTime;
import java.time.ZoneId;

public class DBOperations {

    private DBConnManager connectionManager;

    public DBOperations(String[] dbConfig) {
        this.connectionManager = new DBConnManager(dbConfig);
    }

    private Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    private ResultSet findUser(String userName) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_name = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, userName);
            return pstmt.executeQuery();
        }
    }

    private ResultSet findChatGroup(String chatName) throws SQLException {
        String sql = "SELECT * FROM chat_groups WHERE chat_name = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, chatName);
            return pstmt.executeQuery();
        }
    }

    public boolean registerUser(String userName, String userPassword) throws SQLException {
        try (ResultSet rs = findUser(userName)) {
            if (rs.next()) {
                return false;// 用户已存在
            }
            String sql = "INSERT INTO users (user_name, user_password) VALUES (?, ?)";
            try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                // stmt.setString(1, userId);
                stmt.setString(2, userName);
                stmt.setString(3, userPassword);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;// 影响一行
            }
        }
    }

    public boolean loginUser(String userName, String userPassword) throws SQLException {
        try (ResultSet rs = findUser(userName)) {
            if (rs.next()) {
                return rs.getString("user_password").equals(userPassword);// ? 1 : 0;
            }
            return false;
        }
    }

    /**
     * 增量同步
     * 
     * @param userId        用户 ID
     * @param fromTimestamp 从哪个时间戳开始获取消息
     * @return 返回自指定时间戳以来的消息列表
     * @throws SQLException
     */
    public List<Message> getMessagesSince(int userId, Timestamp fromTimestamp) throws SQLException {
        List<Message> messages = new ArrayList<>();

        // 使用联合查询获取用户参与的所有聊天 ID 以及相关消息
        String sql = "SELECT cm.message_id, cm.sender_id, cm.chat_id, cm.content, cm.timestamp " +
                "FROM chat_messages cm " +
                "JOIN user_chat_map ucm ON cm.chat_id = ucm.chat_id " +
                "WHERE ucm.user_id = ? AND cm.timestamp > ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            // 设置用户 ID 参数
            stmt.setInt(1, userId);
            // 设置时间戳参数
            stmt.setTimestamp(2, fromTimestamp);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int messageID = rs.getInt("message_id");
                    int senderID = rs.getInt("sender_id");
                    int chatID = rs.getInt("chat_id");
                    String content = rs.getString("content");
                    Timestamp timestamp = rs.getTimestamp("timestamp");

                    ZonedDateTime zonedDateTime = timestamp.toInstant().atZone(ZoneId.of("CST"));// 北京时间同步

                    TextMessage message = new TextMessage(senderID, chatID, content, messageID, zonedDateTime, "chat");
                    messages.add(message);
                }
            }
        }
        return messages;
    }

    // private ResultSet findChatGroup(int chatID) throws SQLException {
    // String sql = "SELECT * FROM chat_groups WHERE chat_id = ?";
    // try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
    // pstmt.setInt(1, chatID);
    // return pstmt.executeQuery();
    // }
    // }

    public boolean createChatGroup(int userID, String chatName, String groupPassword) throws SQLException {
        String sql = "INSERT INTO chat_groups (chat_name,chat_password) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, chatName);
            stmt.setString(2, groupPassword);
            int rowsAffected = stmt.executeUpdate();
            joinChatGroup(userID, chatName, groupPassword);
            return rowsAffected > 0;
        }
    }

    public boolean joinChatGroup(int userID, String chatName, String chatPassword) throws SQLException {

        try (ResultSet rs = findChatGroup(chatName)) {
            if (rs.next()) {
                if (rs.getString("chat_password").equals(chatPassword)) {
                    String sql = "INSERT INTO user_chat_map (user_id, chat_id) VALUES (?, ?)";
                    try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                        stmt.setInt(1, userID);
                        stmt.setString(2, chatName);
                        int rowsAffected = stmt.executeUpdate();
                        return rowsAffected > 0;
                    }
                }
            }
            return false;
        }
    }

    public boolean leaveChatGroup(int userId, String chatId) throws SQLException {
        String sql = "DELETE FROM user_chat_map WHERE user_id = ? AND chat_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, chatId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean recordMessage(String messageId, String senderId, String chatId, String content)
            throws SQLException {
        String sql = "INSERT INTO group_messages (message_id, sender_id, chat_id, content) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, messageId);
            stmt.setString(2, senderId);
            stmt.setString(3, chatId);
            stmt.setString(4, content);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean recordMessage(TextMessage textMessage) throws SQLException {
        String sql = "INSERT INTO chat_messages (message_id, sender_id, chat_id, content) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, textMessage.getMessageId());
            stmt.setInt(2, textMessage.getSenderId());
            stmt.setInt(3, textMessage.getChatId());
            stmt.setString(4, textMessage.getContent());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    row.put(columnName, columnValue);
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Map<String, Object> row = new HashMap<>();
            row.put("查询异常 ", sql);
            resultList.add(row);
        }
        return resultList;
    }

    public int executeUpdate(String sql) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean reconnectDatabase(String[] dbConfig) {
        this.connectionManager = new DBConnManager(dbConfig);
        return testConnection();
    }

    public boolean testConnection() {
        String testQuery = "SELECT 1";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(testQuery);
                ResultSet rs = pstmt.executeQuery()) {
            return rs.next(); // 如果有结果返回，则连接有效
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 出现异常，连接无效
        }
    }

    public String findUserName(int userID) throws SQLException {
        String sql = "SELECT user_name FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_name");
                }
                return "未找到用户";
            }
        }
    }

    public int findUserID(String userName) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE user_name = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
                return -1;
            }
        }
    }

    public String findChatName(int chatID) throws SQLException {
        String sql = "SELECT chat_name FROM users WHERE chat_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, chatID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("chat_name");
                }
                return "未找到用户";
            }
        }
    }

    public int findChatID(String chatName) throws SQLException {
        String sql = "SELECT chat_id FROM users WHERE chat_name = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, chatName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("chat_id");
                }
                return -1;
            }
        }
    }

    public List<Chat> findChatGroups(int userID) throws SQLException {
        List<Chat> chatGroups = new ArrayList<>();

        String sql = "SELECT cg.chat_id, cg.chat_name, u.user_name " +
                "FROM chat_groups cg " +
                "JOIN user_chat_map ucm ON cg.chat_id = ucm.chat_id " +
                "JOIN users u ON ucm.user_id = u.user_id " +
                "WHERE ucm.user_id = ?";

        Map<Integer, Chat> chatMap = new HashMap<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int chatID = rs.getInt("chat_id");
                    String chatName = rs.getString("chat_name");
                    String userName = rs.getString("user_name");

                    if (!chatMap.containsKey(chatID)) {
                        Chat chat = new Chat(chatID, chatName);
                        chatMap.put(chatID, chat);
                    }
                    chatMap.get(chatID).addParticipant(userName);
                }
            }
        }

        chatGroups.addAll(chatMap.values());
        return chatGroups;
    }

}
