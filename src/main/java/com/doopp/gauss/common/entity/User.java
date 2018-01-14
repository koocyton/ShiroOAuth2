package com.doopp.gauss.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * user entity
 */

@Data
public class User implements Serializable {

    // 编号
    private Long id;

    // 昵称
    private String nickName;

    // 国家
    private String country;

    // 登录时间
    private String loginTime;

    // 性别
    private int gender;

    // 头像
    private String portrait;

    // 好友
    private String friends;
}
