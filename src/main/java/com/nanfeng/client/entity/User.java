package com.nanfeng.client.entity;

import lombok.Data;

import java.util.Set;

/**
 * Author：nanfeng
 * Created:2019/8/12
 * 实体类
 */
@Data
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String brief;

    private Set<String> userNames;
}
