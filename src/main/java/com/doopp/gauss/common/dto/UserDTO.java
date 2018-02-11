package com.doopp.gauss.common.dto;

import lombok.Data;

/**
 * 来用对外开发的 User
 * @author Administrator
 *
 */
@Data
public class UserDTO {

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
}
