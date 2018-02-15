package com.doopp.gauss.common.entity;

import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.defined.PlayerStatus;
import lombok.Getter;
import lombok.Setter;


public class Player {

    // 编号
    @Getter @Setter
    private Long id;

    // 昵称
    @Getter @Setter
    private String nickname;

    // 头像
    @Getter @Setter
    private String avatar_url;

    // 游戏状态
    @Getter @Setter
    private String status = PlayerStatus.WAITING;

    // 游戏里的身份
    @Getter @Setter
    private Identity identity;

    // 所在房间
    @Getter
    private int room_id;

    // 座位编号
    @Getter @Setter
    private int seat;

    // 是否存活
    public boolean isLiving() {
        return !this.status.equals(PlayerStatus.DEATH) && !this.status.equals(PlayerStatus.LEAVE);
    }

    public void setRoom_id(int room_id) {
        if (this.room_id==0 && room_id!=0) {
            this.room_id = room_id;
        }
    }
}
