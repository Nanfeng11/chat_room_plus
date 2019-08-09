package com.nanfeng;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.nanfeng.util.CommUtils;
import org.junit.Test;



import java.sql.*;
import java.util.Properties;

/**
 * Author：nanfeng
 * Created:2019/8/9
 */
public class JDBCTest {
    //加载数据源
    private static DruidDataSource dataSource;

    static {
        Properties props = CommUtils.loadProperties("datasource.properties");
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQuery() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String sql = "SELECT * FROM user";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("username");
                String password = resultSet.getString("password");
                String brief = resultSet.getString("brief");
                System.out.println("id为：" + id + "，用户名为：" + userName + ",密码为：" + password + "，简介为：" + brief);
            }
        } catch (SQLException e) {

        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    public void testInsert(){
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = (Connection) dataSource.getPooledConnection();
            String sql = "INSERT INTO user(username, password, brief) "+"VALUES (?,?,?)";
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            int rows = statement.executeUpdate();
        }catch (SQLException e){

        }finally {
            closeResources(connection,statement);
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
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
