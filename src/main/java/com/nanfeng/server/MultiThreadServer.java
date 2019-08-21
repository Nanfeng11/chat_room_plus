package com.nanfeng.server;

import com.nanfeng.util.CommUtils;
import com.nanfeng.vo.MessageVO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author：nanfeng
 * Created:2019/8/14
 * 聊天室服务端
 */
public class MultiThreadServer {
    private static final String IP;
    private static final int PORT;

    //缓存当前服务器所有在线的客户端信息
    private static Map<String, Socket> clients = new ConcurrentHashMap<>();
    //缓存当前服务器注册的所有群名称以及群好友
    private static Map<String,Set<String>> groups = new ConcurrentHashMap<>();

    static {
        Properties pros = CommUtils.loadProperties("socket.properties");
        IP = pros.getProperty("address");
        PORT = Integer.parseInt(pros.getProperty("port"));
    }

    //处理每个客户端连接的时候，需要创建新的线程去处理
    //处理每个客户端连接的内部类
    private static class ExecuteClient implements Runnable {
        private Socket client;
        private Scanner in;
        private PrintStream out;

        public ExecuteClient(Socket client) {
            this.client = client;
            try {
                this.in = new Scanner(client.getInputStream());
                this.out = new PrintStream(client.getOutputStream(), true, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            //不断监听客户端连接
            while (true) {
                //判断客户端有输入
                if (in.hasNextLine()) {
                    //json对象
                    String jsonStrFromClient = in.nextLine();
                    //反序列化
                    MessageVO msgFromClient = (MessageVO) CommUtils.json2Object(jsonStrFromClient, MessageVO.class);

                    if (msgFromClient.getType().equals("1")) {
                        //新用户注册到服务端
                        String userName = msgFromClient.getContent();
                        //将当前在线的所有用户名发回客户端
                        MessageVO msg2Client = new MessageVO();
                        msg2Client.setType("1");
                        msg2Client.setContent(CommUtils.object2Json(clients.keySet()));
                        out.println(CommUtils.object2Json(msg2Client));

                        //将新上线的用户信息发回给当前已在线的所有用户
                        sendUserLogin("newLogin:"+userName);
                        //将当前新用户注册到服务端缓存
                        clients.put(userName,client);
                        System.out.println(userName+"上线了！");
                        System.out.println("当前聊天室共有"+clients.size()+"人！");
                    }else if (msgFromClient.getType().equals("2")){
                        //用户私聊
                        /**
                         * type:2
                         * Content:myName-msg
                         * to:friendName
                         */
                        String friendName = msgFromClient.getTo();
                        Socket clientSocket = clients.get(friendName);
                        try {
                            PrintStream out = new PrintStream(clientSocket.getOutputStream(),true,"UTF-8");
                            //服务端不用拆出消息，只需要查看要发给谁
                            MessageVO msg2Client = new MessageVO();
                            msg2Client.setType("2");
                            msg2Client.setContent(msgFromClient.getContent());
                            System.out.println("收到私聊信息，内容为："+msgFromClient.getContent());
                            out.println(CommUtils.object2Json(msg2Client));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if (msgFromClient.getType().equals("3")){
                        //注册群
                        String groupName = msgFromClient.getContent();
                        //该群的所有群成员
                        Set<String> friends = (Set<String>) CommUtils.json2Object(msgFromClient.getTo(),Set.class);
                        groups.put(groupName,friends);
                        System.out.println("有新的群注册成功，群名称为："+groupName+",一共有"+groups.size()+"个群");
                    }else if (msgFromClient.getType().equals("4")){
                        //群聊信息
                        System.out.println("服务器收到的群聊信息为："+msgFromClient);
                        //给哪个群发
                        String groupName = msgFromClient.getContent();
                        Set<String> names = groups.get(groupName);
                        Iterator<String> iterator = names.iterator();
                        while (iterator.hasNext()){
                            String socketName = iterator.next();
                            Socket client = clients.get(socketName);
                            try {
                                PrintStream out = new PrintStream(client.getOutputStream(),true,"UTF-8");
                                //信息的转发
                                MessageVO messageVO = new MessageVO();
                                messageVO.setType("4");
                                messageVO.setContent(msgFromClient.getContent());
                                //群名-[好友列表]，第一次收到群消息，并不知道自己在这个群里，需要保存群信息（群名，群列表）
                                messageVO.setTo(groupName+"-"+CommUtils.object2Json(names));
                                out.println(CommUtils.object2Json(messageVO));
                                System.out.println("服务端发送的群聊信息为："+messageVO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        /**
         * 向所有在线用户发送新用户上线信息
         * @param msg
         */
        private void sendUserLogin(String msg) {
            //<用户名，每个用户的socket>
            for (Map.Entry<String, Socket> entry : clients.entrySet()) {
                //取出当前实体的每一个Socket
                Socket socket = entry.getValue();
                try {
                    //调用输出流
                    PrintStream out = new PrintStream(socket.getOutputStream(), true, "UTF-8");
                    out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        //创建线程
        ExecutorService executors = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 50; i++) {
            System.out.println("等待客户端连接。。。");
            Socket client = serverSocket.accept();
            System.out.println("有新的连接，端口号为" + client.getPort());
            //具体的连接交给子线程处理，服务器侦听新的客户端连接
            executors.submit(new ExecuteClient(client));
        }
    }
}
