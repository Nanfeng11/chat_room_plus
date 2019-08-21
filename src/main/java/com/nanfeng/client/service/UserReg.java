package com.nanfeng.client.service;

import com.nanfeng.client.dao.AccountDao;
import com.nanfeng.client.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author：nanfeng
 * Created:2019/8/12
 */
public class UserReg {
    private JPanel userRegPanel;
    private JTextField usernameText;
    private JPasswordField passwordText;
    private JTextField briefText;
    private JButton regBtn;

    //数据库增删查改的用户类
    private AccountDao accountDao = new AccountDao();

    //通过主界面弹起，不能使用main方法
    //每个弹起的页面都是一个类，产生一个类就产生一个类对象，使用构造方法
    public UserReg(){
        JFrame frame = new JFrame("用户注册");
        frame.setContentPane(userRegPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //界面居中
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        //点击注册按钮，获取输入框内容，将信息持久化到db中，成功弹出提示框
        regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取用户输入的注册信息
                String userName = usernameText.getText();
                String password = String.valueOf(passwordText.getPassword());
                String brief = briefText.getText();
                //将输入信息包装为User类，保存到数据库中
                User user = new User();
                user.setUserName(userName);
                user.setPassword(password);
                user.setBrief(brief);
                //调用Dao对象（根据userReg返回值知道，注册成功还是失败）
                if (accountDao.userReg(user)){
                    //弹出提示框（父组件是谁，在谁的基础上弹出；需要提示的信息；提示框的标题；提示信息的类型）
                    //返回登录页面
                    JOptionPane.showMessageDialog(frame,"注册成功！","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    //关掉注册页面
                    frame.setVisible(false);
                }else {
                    //弹出提示框，保留当前注册页面
                    JOptionPane.showMessageDialog(frame,"注册失败！","错误信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
