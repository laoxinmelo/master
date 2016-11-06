package com.raul.bupt.db.impl;

import com.raul.bupt.db.DBTool;

import java.sql.*;

/**
 * Created by Administrator on 2016/11/5.
 */
public class DBToolImpl implements DBTool {

    // 驱动程序名
    private final String driver = "com.mysql.jdbc.Driver";
    // URL指向要访问的数据库名scutcs
    private final String url = "jdbc:mysql://127.0.0.1:3306/tmall?characterEncoding=utf-8";   //考虑中文编码问题
    // MySQL配置时的用户名
    private final String user = "root";
    // MySQL配置时的密码
    private final String password = "lx920618";

    private Statement statement;
    private Connection conn;

    public DBToolImpl() {
        try {
            // 加载驱动程序
            Class.forName(driver);

            // 连续数据库
            this.conn = DriverManager.getConnection(url, user, password);

//            if (!this.conn.isClosed())
//                System.out.println("Succeeded connecting to the Database!");

            // statement用来执行SQL语句
            this.statement = this.conn.createStatement();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据查询...
     * @param sql
     * @return
     */
    public ResultSet query(String sql) {
        try {
            return this.statement.executeQuery(sql);
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 数据插入、更新、删除...
     * @param sql
     */
    public void execute(String sql) {
        try {
            this.statement.executeUpdate(sql);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
