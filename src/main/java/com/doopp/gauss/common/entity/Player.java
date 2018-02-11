package com.doopp.gauss.common.entity;

import com.doopp.gauss.common.defined.Identity;
import lombok.Data;

@Data
public class Player {

    // 编号
    private Long id;

    // 昵称
    private String nickname;

    // 头像
    private String avatar_url;

    // 游戏状态
    private int status = 1;

    // 游戏里的身份
    private Identity identity;

    // 所在房间
    private int room_id;

    // 座位编号
    private int seat;

    // 是否存活
    public boolean isLiving() {
        return (this.status==1);
    }
}
