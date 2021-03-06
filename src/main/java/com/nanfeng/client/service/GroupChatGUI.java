package com.nanfeng.client.service;

import com.nanfeng.util.CommUtils;
import com.nanfeng.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

/**
 * Author：nanfeng
 * Created:2019/8/20
 */
public class GroupChatGUI {
    private JPanel groupPanel;
    private JTextArea readFromServer;
    private JTextField send2Server;
    private JPanel friendsPanel;

    private JFrame frame;

    private String groupName;
    private Set<String> friends;
    private String myName;
    private Connect2Server connect2Server;

    public GroupChatGUI(String groupName,Set<String> friends,String myName,Connect2Server connect2Server) {
        this.groupName = groupName;
        this.friends = friends;
        this.myName = myName;
        this.connect2Server = connect2Server;

        frame = new JFrame(groupName);
        frame.setContentPane(groupPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400, 400);
        frame.setVisible(true);

        //加载群中的好友列表
        friendsPanel.setLayout(new BoxLayout(friendsPanel,BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while (iterator.hasNext()){
            String labelName = iterator.next();
            JLabel jLabel = new JLabel(labelName);
            friendsPanel.add(jLabel);
        }

        //收发消息
        send2Server.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(send2Server.getText());
                //捕捉回车按键
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    String str2Server = sb.toString();
                    //type:4
                    //content:myName-msg
                    //to:groupName
                    MessageVO messageVO = new MessageVO();
                    messageVO.setType("4");
                    messageVO.setContent(myName+"-"+str2Server);
                    messageVO.setTo(groupName);
                    try {
                        PrintStream out = new PrintStream(connect2Server.getOut(),true,"UTF-8");
                        out.println(CommUtils.object2Json(messageVO));
                        System.out.println("客户端发送的群聊信息为："+messageVO);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
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
