package com.doopp.gauss.api.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 房间的实体
 *
 * Created by Henry on 2017/8/26.
 */
public class RoomEntity implements Serializable {

    // 房间 ID
    @Getter @Setter private int id;

    // 房间有多少个座位，即最多坐多少人
    @Getter @Setter private int seatCount;

    // 房间名
    @Getter @Setter private String name;

    // 房主
    @Getter @Setter private UserEntity owner;

    // 前排玩家
    @Getter private Map<Long, UserEntity> frontUsers = new HashMap<>();

    // 围观玩家
    @Getter private Map<Long, UserEntity> watchUsers = new HashMap<>();

    // 加入到前排
    public void joinFront(UserEntity user) {
        this.frontUsers.put(user.getId(), user);
        this.watchUsers.remove(user.getId());
    }

    // 加入到围观玩家
    public void joinWatch(UserEntity user) {
        this.frontUsers.remove(user.getId());
        this.watchUsers.put(user.getId(), user);
    }
}
