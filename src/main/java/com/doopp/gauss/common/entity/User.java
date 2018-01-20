package com.doopp.gauss.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * user entity
 */

@Data
public class User {

    // 编号
    private Long id;

    // 昵称
    private String nickName;

    // 国家
    private String country;

    // 登录时间
    private Long loginTime;

    // 性别
    private int gender;

    // 头像
    private String portrait;

    // 好友
    private String friends;

    // 游戏状态
    private int status;

    // 游戏里的身份
    private String identity;

    public boolean isLiving() {
        return (this.status==1);
    }
}
