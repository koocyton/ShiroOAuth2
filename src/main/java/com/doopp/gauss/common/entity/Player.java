package com.doopp.gauss.common.entity;

import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.defined.PlayerStatus;
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
    private String status = PlayerStatus.WAITING;

    // 游戏里的身份
    private Identity identity;

    // 所在房间
    private int room_id;

    // 座位编号
    private int seat;

    // 是否存活
    public boolean isLiving() {
        return !this.status.equals(PlayerStatus.DEATH) && !this.status.equals(PlayerStatus.LEAVE);
    }
}
