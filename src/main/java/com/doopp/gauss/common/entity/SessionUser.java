package com.doopp.gauss.common.entity;

import lombok.Data;

/**
 * user entity
 */
@Data
public class SessionUser {

    // 编号
    private Long id;

    // 昵称
    private String nickname;

    // 国家
    private String country;

    // 性别
    private int gender;

    // 头像
    private String avatar_url;

    // 好友
    private String friends;

    // 得分
    private int score;

    // 排名
    private int ranking;

    // 金币
    private int gold;

    // session token
    private String session_token;
}
