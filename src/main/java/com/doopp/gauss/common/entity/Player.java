package com.doopp.gauss.common.entity;

import lombok.Data;

@Data
public class Player {

    // 编号
    private Long id;

    // 昵称
    private String nickName;

    // 头像
    private String portrait;

    // 游戏状态
    private int status;

    // 游戏里的身份
    private String identity;

    // 所在房间
    private int roomId;

    // 是否存活
    public boolean isLiving() {
        return (this.status==1);
    }
}
