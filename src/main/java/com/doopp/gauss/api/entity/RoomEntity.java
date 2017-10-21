package com.doopp.gauss.api.entity;

import com.doopp.gauss.api.service.impl.AccountServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 房间的实体
 *
 * Created by Henry on 2017/8/26.
 */
public class RoomEntity implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(RoomEntity.class);

    // 房间 ID
    @Getter @Setter private int id;

    // 房间有多少个座位，即最多坐多少人
    @Getter @Setter private int seatCount;

    // 房间名
    @Getter @Setter private String name;

    // 房主
    @Getter @Setter private UserEntity owner;

    // 前排玩家
    @Getter private Map<String, UserEntity> frontUsers = new HashMap<>();

    // 围观玩家
    @Getter private Map<String, UserEntity> watchUsers = new HashMap<>();

    // 加入到前排
    public void joinFront(UserEntity user) {
        this.frontUsers.put(String.valueOf(user.getId()), user);
        this.watchUsers.remove(String.valueOf(user.getId()));
    }

    // 加入到围观玩家
    public void joinWatch(UserEntity user) {
        this.frontUsers.remove(String.valueOf(user.getId()));
        this.watchUsers.put(String.valueOf(user.getId()), user);
    }

    // 离开房间
    public void userLeave(UserEntity user) {
        if (this.owner!=null && this.owner.getId().equals(user.getId())) {
            this.owner = null;
        }
        this.frontUsers.remove(String.valueOf(user.getId()));
        this.watchUsers.remove(String.valueOf(user.getId()), user);
    }
}
