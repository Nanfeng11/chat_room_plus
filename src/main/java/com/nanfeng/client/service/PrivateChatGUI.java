package com.nanfeng.client.service;

import com.nanfeng.util.CommUtils;
import com.nanfeng.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Author：nanfeng
 * Created:2019/8/19
 */
public class PrivateChatGUI {

    private JPanel PrivateChatPanel;
    private JTextArea readFromServer;
    private JTextField send2Server;

    private String friendName;
    private String myName;
    private Connect2Server connect2Server;
    private JFrame frame;

    private PrintStream out;

    public PrivateChatGUI(String friendName, String myName, Connect2Server connect2Server) {
        this.friendName = friendName;
        this.myName = myName;
        this.connect2Server = connect2Server;

        try {
            this.out = new PrintStream(connect2Server.getOut(),true,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        frame = new JFrame("与" + friendName + "私聊中。。。");
        frame.setContentPane(PrivateChatPanel);

        //设置窗口关闭的操作：私聊的时候不用退客户端，只是隐藏窗口
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(true);

        //捕捉输入框的键盘输入
        send2Server.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //把所有东西拼接到一个字符串，然后按下Enter发给服务器
                StringBuilder sb = new StringBuilder();
                sb.append(send2Server.getText());
                //当捕捉到按下Enter键
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    //将当前信息发送到服务端
                    String msg = sb.toString();

                    MessageVO messageVO = new MessageVO();
                    messageVO.setType("2");
                    messageVO.setContent(myName+"-"+msg);
                    messageVO.setTo(friendName);
                    //keyAdapter抽象类，this是匿名内部类的当前对象，我要获取的是外部类PrivateChatGUI的对象
                    PrivateChatGUI.this.out.println(CommUtils.object2Json(messageVO));

                    //将自己发送的信息展示到当前私聊界面
                    readFromServer(myName+"说："+msg);
                    //还原输入框
                    send2Server.setText("");
                }
            }
        });
    }

    public void readFromServer(String msg){
        readFromServer.append(msg+"\n");
    }

    public JFrame getFrame() {
        return frame;
    }
}
