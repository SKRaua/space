package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnManager {

    private String[] dbConfig;

    public DBConnManager(String[] dbConfig) {
        if (dbConfig.length != 3) {
            throw new IllegalArgumentException("配置数组为URL、用户名和密码");
        }
        this.dbConfig = dbConfig;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(dbConfig[0], dbConfig[1], dbConfig[2]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("JDBC 驱动程序未找到", e);
        }
    }
}
