package com.doopp.gauss.api.entity.dto;

import lombok.Data;

/**
 * 来用对外开发的 UserEntity
 * @author Administrator
 *
 */
@Data
public class UserMeDTO {

    // 编号
    private Long id;

    // 账号
    private String account;

    // 昵称
    private String nickname;

    // 性别
    private int gender;

    // 头像
    private String portrait;

    // 好友
    private String friends;
}
