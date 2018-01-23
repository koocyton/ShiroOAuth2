package com.doopp.gauss.common.entity;

import com.doopp.gauss.common.defined.Identity;
import lombok.Data;

@Data
public class Player {

    public final String WOF_ID = "wolf";
    public static String VLG_ID = "villager";
    public static String WIH_ID = "witch";
    public static String HNT_ID = "hunter";
    public static String SER_ID = "seer";

    // 编号
    private Long id;

    // 昵称
    private String nickName;

    // 头像
    private String portrait;

    // 游戏状态
    private int status;

    // 游戏里的身份
    private Identity identity;

    // 所在房间
    private int roomId;

    // 是否存活
    public boolean isLiving() {
        return (this.status==1);
    }
}
