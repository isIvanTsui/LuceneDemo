package com.ivan.search;

import org.sqlite.mc.SQLiteMCConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 加解密sqlite数据库
 *
 * @author cuiyingfan
 * @date 2022/03/10
 */
public class Main {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            String dbPath = "D:\\Users\\cuiyingfan\\Desktop\\MyDemo2\\src\\main\\resources\\mcdex_raw.db";
            SQLiteMCConfig.Builder builder = new SQLiteMCConfig.Builder();
            builder.withKey("123456"); //连接已加密库
            connection = builder.createConnection("jdbc:sqlite:" + dbPath);


            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            //第一次加密
            statement.executeUpdate("PRAGMA rekey='123456'");

            ResultSet rs = statement.executeQuery("select count(*) from cpr_main");

            while (rs.next()) {
                // read the result set
                System.out.println("count = " + rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }
}
