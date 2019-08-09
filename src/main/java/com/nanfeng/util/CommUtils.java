package com.nanfeng.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 封装公共工具方法，如加载配置文件、json序列化等
 * Author：nanfeng
 * Created:2019/8/9
 */
public class CommUtils {
    /**
     * 加载配置文件   Properties资源文件类
     * @param fileName  要加载的配置文件名称
     * @return
     */
    public static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        //获取类加载器（资源文件与类在同一路径下），把资源变成输入流
        InputStream in = CommUtils.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(in);
        } catch (IOException e) {
            return null;
        }
        return properties;
    }
}
