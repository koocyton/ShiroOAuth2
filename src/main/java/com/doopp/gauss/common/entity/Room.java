package com.doopp.gauss.common.entity;

import lombok.Data;

/**
 * 房间的实体
 *
 * Created by Henry on 2017/10/26.
 */
@Data
public class Room {

    // 房间 ID
    int id;

    // 房间的用户
    User[] users = {
        null, null, null, null, null, null,
        null, null, null, null, null, null,
    };
}
