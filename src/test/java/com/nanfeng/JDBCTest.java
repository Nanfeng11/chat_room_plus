package com.nanfeng;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.nanfeng.util.CommUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;


import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Properties;

/**
 * Author：nanfeng
 * Created:2019/8/9
 */
public class JDBCTest {
    //加载数据源
    private static DruidDataSource dataSource;

    //初始化静态资源
    // 静态代码块，在类加载的时候执行，只执行一次
    static {
        Properties props = CommUtils.loadProperties("datasource.properties");
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
//    public void testQuery() {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        try {
//            connection = (Connection) dataSource.getPooledConnection();
//            String sql = "SELECT * FROM user";
//            statement = connection.prepareStatement(sql);
//            resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String userName = resultSet.getString("username");
//                String password = resultSet.getString("password");
//                String brief = resultSet.getString("brief");
//                System.out.println("id为：" + id + "，用户名为：" + userName + ",密码为：" + password + "，简介为：" + brief);
//            }
//        } catch (SQLException e) {
//
//        } finally {
//            closeResources(connection, statement, resultSet);
//        }
//    }
    public void testQuery() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            String user = "test";
            //"test ' --" 即使SQL注入，也会失败，prepareStatement有占位符，认为它就是用户名，不做特殊处理，所以用prepareStatement，防注入
            String pass = "123";
            statement.setString(1, user);
            statement.setString(2, pass);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("登录成功");
            } else {
                System.out.println("登录失败");
            }
        } catch (SQLException e) {

        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    @Test
    public void testInsert() throws UnsupportedEncodingException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String password = DigestUtils.md5Hex("123");
            String sql = "INSERT INTO user(username, password, brief) " + "VALUES (?,?,?)";
//            String brief = new String("测试".getBytes(),"UTF-8");
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, "test1");
            statement.setString(2, password);
            statement.setString(3,"西洲");
            int rows = statement.executeUpdate();
            Assert.assertEquals(1, rows);
        } catch (SQLException e) {

        } finally {
            closeResources(connection, statement);
        }
    }

    @Test
    public void testLogin() {
        String userName = "test";
        String password = "123";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String sql = "SELECT * FROM user WHERE username = '" + userName + "'" +
                    "AND password = '" + password + "'";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                System.out.println("登录成功");
            } else {
                System.out.println("登录失败");
            }
        } catch (SQLException e) {

        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    public void closeResources(Connection connection, Statement statement) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        closeResources(connection, statement);
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
