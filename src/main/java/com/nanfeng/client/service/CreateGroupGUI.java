package com.nanfeng.client.service;

import com.nanfeng.util.CommUtils;
import com.nanfeng.vo.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Author：nanfeng
 * Created:2019/8/19
 */
public class CreateGroupGUI {
    private JPanel createGroupPanel;
    private JPanel friendLabelPanel;
    private JTextField groupNameText;
    private JButton conformBtn;

    private String myName;
    private Set<String> friends;
    private Connect2Server connect2Server;
    private FriendsList friendsList;


    public CreateGroupGUI(String myName, Set<String> friends,
                          Connect2Server connect2Server,
                          FriendsList friendsList) {
        this.myName = myName;
        this.friends = friends;
        this.connect2Server = connect2Server;
        this.friendsList = friendsList;

        JFrame frame = new JFrame("创建群组");
        frame.setContentPane(createGroupPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        //居中
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //将在线好友以checkBox展示到界面中
        //纵向展示
        friendLabelPanel.setLayout(new BoxLayout(friendLabelPanel, BoxLayout.Y_AXIS));
        JCheckBox[] checkBoxes = new JCheckBox[friends.size()];
        Iterator<String> iterator = friends.iterator();
        int i = 0;
        //动态遍历在线好友，每遍历一次，添加一个带勾的标签
        while (iterator.hasNext()) {
            String labelName = iterator.next();
            checkBoxes[i] = new JCheckBox(labelName);
            //JCheckBox checkBox = new JCheckBox(labelName);
            friendLabelPanel.add(checkBoxes[i]);
            //friendLabelPanel.add(checkBox);
            i++;
        }
        //刷新
        friendLabelPanel.revalidate();

        //点击提交按钮提交信息到服务端
        conformBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //1.判断哪些好友选中加入群聊
                Set<String> selectedFriends = new HashSet<>();

                //获取所有组件
                Component[] comps = friendLabelPanel.getComponents();
                for (Component comp : comps) {
                    //顶层父类Component里有很多组建，强转，变成具体子类
                    JCheckBox checkBox = (JCheckBox) comp;
                    if (checkBox.isSelected()) {
                        String labelName = checkBox.getText();
                        selectedFriends.add(labelName);
                    }
                }
                selectedFriends.add(myName);

                //2.获取群名输入框输入的群名称
                String groupName = groupNameText.getText();

                //3.将群名称+选中好友信息发送到服务端
                /**
                 * type:3
                 * content:groupName
                 * to:[user1,user2,user3...]
                 */
                MessageVO messageVO = new MessageVO();
                messageVO.setType("3");
                messageVO.setContent(groupName);
                messageVO.setTo(CommUtils.object2Json(selectedFriends));
                try {
                    PrintStream out = new PrintStream(connect2Server.getOut(), true, "UTF-8");
                    out.println(CommUtils.object2Json(messageVO));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                //4.将当前创建群的界面隐藏掉，刷新好友列表界面的群列表
                frame.setVisible(false);

                //调用friend里面的loadGroupList方法，调用之前得先加载群组信息
                //addGroupInfo
                //loadGroup
                friendsList.addGroup(groupName, selectedFriends);
                friendsList.loadGroupList();
            }
        });
    }
}
