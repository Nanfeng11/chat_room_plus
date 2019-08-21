package com.nanfeng.client.service;

import com.nanfeng.client.dao.AccountDao;
import com.nanfeng.client.entity.User;
import com.nanfeng.util.CommUtils;
import com.nanfeng.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Set;

/**
 * Author：nanfeng
 * Created:2019/8/12
 */
public class UserLogin {
    private JPanel UserLoginPanel;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JButton regButton;
    private JButton loginButton;
    private JPanel userPanel;
    private JPanel btnPanel;
    private JFrame frame;

    //存储了用户信息
    private AccountDao accountDao = new AccountDao();

    public UserLogin() {
        //frame用户登录之后要注销掉，frame静态，在主方法中无法注销，所以放在构造方法中
        frame = new JFrame("用户登录");
        frame.setContentPane(UserLoginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        //注册按钮
        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //弹出注册页面
                new UserReg();
            }
        });

        //登录按钮
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //校验用户信息
                String userName = userNameText.getText();
                String password = String.valueOf(passwordText.getPassword());

                //从数据库查询数据对不对，AccountDao类中存储量用户信息，根据返回值判断
                User user = accountDao.userLogin(userName,password);
                if (user!=null){
                    //成功，加载用户列表

                    //弹框
                    JOptionPane.showMessageDialog(frame,"登录成功","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    //登录成功后当前页面不可见
                    frame.setVisible(false);

                    //与服务器建立连接，将当前用户的用户名与密码发送到服务端
                    Connect2Server connect2Server = new Connect2Server();

                    //具体通信过程
                    MessageVO msg2Server = new MessageVO();
                    //让服务器知道一个新的用户要注册到我的缓存
                    msg2Server.setType("1");
                    msg2Server.setContent(userName);
                    //把对象变成json字符串发给服务器
                    String json2Server = CommUtils.object2Json(msg2Server);
                    //建立的连接封装在connect2Server，要发送信息需要获取连接的输出流
                    try {
                        //把当前用户名和信息发送到服务端
                        PrintStream out = new PrintStream(connect2Server.getOut(),true,"UTF-8");
                        out.println(json2Server);

                        //读取服务端发回的所有在线用户信息
                        Scanner in = new Scanner(connect2Server.getIn());
                        if (in.hasNextLine()){
                            //服务器发回的响应
                            String msgFromServerStr = in.nextLine();

                            MessageVO msgFronServer = (MessageVO) CommUtils.json2Object(msgFromServerStr,MessageVO.class);
                            Set<String> users = (Set<String>) CommUtils.json2Object(msgFronServer.getContent(),Set.class);
                            System.out.println("所有在线用户为："+users);

                            //加载用户列表界面
                            //将当前用户名，所有在线好友，与服务器建立的连接传递到好友列表界面
                            new FriendsList(userName,users,connect2Server);
                        }
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                }else {
                    //失败，停留在当前登录页面，提示用户信息错误
                    JOptionPane.showMessageDialog(frame,"登录失败！","错误信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        UserLogin userLogin = new UserLogin();
    }
}
