package com.nanfeng.client.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.nanfeng.util.CommUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Author：nanfeng
 * Created:2019/8/12
 * 封装基础dao操作，获取数据源、连接、关闭数据源
 */
public class BasedDao {
    //只需要连接，不需要具体的数据源
    private static DruidDataSource dataSource;
    //数据只加载一次，且在一开始就加载
    static {
        Properties properties = CommUtils.loadProperties("datasource.properties");
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            System.err.println("数据源加载失败");
            e.printStackTrace();
        }
    }

    //获取连接，必须有继承权限，才能拥有连接
    protected DruidPooledConnection getConnection(){
        try {
            return (DruidPooledConnection) dataSource.getPooledConnection();
        }catch (SQLException e){
            System.err.println("数据库连接获取失败");
            e.printStackTrace();
        }
        return null;
    }

    protected void closeResources(Connection connection,PreparedStatement statement){
        if (connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void closeResources(Connection connection,PreparedStatement statement,ResultSet resultSet){
        closeResources(connection,statement);
        if (resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}