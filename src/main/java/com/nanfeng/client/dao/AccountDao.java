package com.nanfeng.client.dao;

import com.nanfeng.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

/**
 * 用户操作
 * Author：nanfeng
 * Created:2019/8/12
 */
public class AccountDao extends BasedDao{
    //用户注册 insert
    public boolean userReg(User user){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            String sql = "INSERT INTO user(username,password,brief)"+"VALUES (?,?,?)";
            //（预编译命令，受影响的行数）
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,user.getUserName());
            //密码加密
            statement.setString(2,DigestUtils.md5Hex(user.getPassword()));
            statement.setString(3,user.getBrief());
            int rows = statement.executeUpdate();
            if (rows == 1){
                return true;
            }
        }catch (SQLException e){
            System.err.println("用户注册失败");
            e.printStackTrace();
        }finally {
            closeResources(connection,statement);
        }
        return false;
    }

    // 用户登录
    // 从数据库返回User类
    public User userLogin(String userName,String password){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            //两个业务，必须开启两个事务，相互隔离
            connection = getConnection();
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1,userName);
            statement.setString(2,DigestUtils.md5Hex(password));
            //执行查询（返回值是结果集，要转成具体对象）
            resultSet = statement.executeQuery();
            if (resultSet.next()){
                User user = getUser(resultSet);
                return user;
            }
        }catch (SQLException e){
            System.err.println("用户登录失败");
            e.printStackTrace();
        }finally {
            closeResources(connection,statement,resultSet);
        }
        return null;
    }

    //结果集转成User类对象
    private User getUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUserName(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setBrief(resultSet.getString("brief"));
        return user;
    }
}
