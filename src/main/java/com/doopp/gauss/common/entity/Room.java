package com.doopp.gauss.common.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 房间的实体
 *
 * Created by Henry on 2017/10/26.
 */
@Data
public class Room {

    // 房间 ID
    private int id;

    // 房间的用户
    private Player[] players = {
        null, null, null, null, null, null,
        null, null, null, null, null, null,
    };

    // 房间的状态     0:没开始    1:都准备   2:游戏中    3:游戏结束
    private int status = 0;

    // 房间游戏开局的类型      0:普通(normal)    1:高阶(high)
    private int gameLevel = 0;
}
