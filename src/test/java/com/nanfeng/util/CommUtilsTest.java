package com.nanfeng.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

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
}