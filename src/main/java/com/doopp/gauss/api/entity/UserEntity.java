package com.doopp.gauss.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * user entity
 */

@Data
public class UserEntity implements Serializable {

    // 编号
    private Long id;

    // 用户名
    private String account;

    // 密码
    private String password;

    // 昵称
    private String nickname;

    // 性别
    private int gender;

    // 创建时间
    private int created_at;

    // 加密密码的盐
    private String salt;

    // 头像
    private String portrait;

    // 好友
    private String friends;
}
