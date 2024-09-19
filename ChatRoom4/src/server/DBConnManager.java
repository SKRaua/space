package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 管理与数据库的基本连接
 */
public class DBConnManager {

    private String[] dbConfig;// 数据库配置，地址，用户名和密码

    /**
     * 获取数据库配置
     * 
     * @param dbConfig 数据库连接的配置信息
     */
    public DBConnManager(String[] dbConfig) {
        if (dbConfig.length != 3) {
            throw new IllegalArgumentException("配置数组为URL、用户名和密码");
        }
        this.dbConfig = dbConfig;
    }

    /**
     * 与数据库连接并返回连接
     * 
     * @return 与数据库的连接
     * @throws SQLException 数据库连接异常
     */
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
