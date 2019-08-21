package com.nanfeng.client.service;

import com.nanfeng.util.CommUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

/**
 * Author：nanfeng
 * Created:2019/8/14
 */
public class Connect2Server {
    private static final String IP;
    private static final int PORT;

    static {
        Properties pros = CommUtils.loadProperties("socket.properties");
        IP = pros.getProperty("address");
        PORT = Integer.parseInt(pros.getProperty("port"));
    }

    private Socket client;
    //获取客户端输入输出流
    private InputStream in;
    private OutputStream out;

    public Connect2Server() {
        try {
            client = new Socket(IP,PORT);
            in = client.getInputStream();
            out = client.getOutputStream();
        } catch (IOException e) {
            System.err.println("与服务器建立连接失败");
            e.printStackTrace();
        }
    }

    //获取服务器发来的信息
    public InputStream getIn(){
        return in;
    }

    //给服务器发信息
    public OutputStream getOut(){
        return out;
    }
}
