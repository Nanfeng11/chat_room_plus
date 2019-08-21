package com.nanfeng.util;

import com.nanfeng.client.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Author：nanfeng
 * Created:2019/8/9
 */
public class CommUtilsTest {

    @Test
    public void loadProperties() {
        String fileName = "datasource.properties";
        Properties properties = CommUtils.loadProperties(fileName);
//        System.out.println(properties);
        Assert.assertNotNull(properties);
    }

    @Test
    public void object2Json() {
        //序列化都是深拷贝
        //深拷贝：内部的其他类也创建一个对象
        //浅拷贝：拷贝的对象如果内部包含其他类对象，只是对原来对象的一个复制，并没有产生新对象
        User user = new User();
        user.setId(1);
        user.setUserName("test");
        user.setPassword("123");
        user.setBrief("哈喽");
        Set<String> strings = new HashSet<>();
        strings.add("test1");
        strings.add("test2");
        strings.add("test3");
        user.setUserNames(strings);
        String str = CommUtils.object2Json(user);
        System.out.println(str);
    }

    @Test
    public void json2Object() {
        String jsonStr = "{\"id\":1,\"userName\":\"test\",\"password\":\"123\",\"brief\":\"哈喽\",\"userNames\":[\"test2\",\"test3\",\"test1\"]}";
        User user = (User) CommUtils.json2Object(jsonStr,User.class);
        System.out.println(user.getUserNames());
    }
}