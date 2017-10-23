package com.doopp.gauss.api.entity;

import lombok.Data;

@Data
public class RoomAbstractEntity {

    // 房间 ID
    private int id;

    // 房间人数
    private int userNumber;

    // 房间名
    private String name;

    // 房主名
    private String ownerName;

    // 房间人气 ( 累积送礼点数 )
    private int hot;
}
