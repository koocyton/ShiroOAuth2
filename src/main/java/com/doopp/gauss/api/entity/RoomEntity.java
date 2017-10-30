package com.doopp.gauss.api.entity;

import com.google.common.base.Objects;
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

    /**
     * 活动类型
     */
    private enum ActivityStatus {
        Non,
        Ready,
        InActivity
    }

    private final Logger logger = LoggerFactory.getLogger(RoomEntity.class);

    // 房间 ID
    @Getter @Setter private int id;

    // 房间有多少个座位，即最多坐多少人
    @Getter @Setter private int seatCount;

    // 房间名
    @Getter @Setter private String name;

    // 房主
    @Getter private UserEntity owner;

    // 前排玩家
    @Getter private Map<Long, UserEntity> frontUsers = new HashMap<>();

    // 围观玩家
    @Getter private Map<Long, UserEntity> watchUsers = new HashMap<>();

    // 报名参加活动的玩家 或 正在活动的玩家
    @Getter private Map<Long, UserEntity> activityUsers = new HashMap<>();

    // 活动状态
    @Getter private ActivityStatus activityStatus = ActivityStatus.Non;

    // 活动类型
    @Getter private String activityType = "";

    // 加入到房主
    public void setOwner(UserEntity user) {
        if (this.getOwner()==null) {
            this.frontUsers.remove(user.getId());
            this.watchUsers.remove(user.getId());
            this.owner = user;
        }
    }

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

    // 离开房间
    public void userLeave(UserEntity user) {
        if (this.owner!=null && Objects.equal(this.owner.getId(), user.getId())) {
            this.owner = null;
        }
        this.frontUsers.remove(user.getId());
        this.watchUsers.remove(user.getId());
    }

    // 加入活动
    public void joinActivity(UserEntity user) {

    }


    // 检查活动状态
    public boolean checkActivityStatus(ActivityStatus activityStatus) {
        if (activityStatus.equals(ActivityStatus.Non)) {
            return true;
        }
        else if (activityStatus.equals(ActivityStatus.Ready)) {
            return true;
        }
        else if (activityStatus.equals(ActivityStatus.InActivity)) {
            return true;
        }
        return false;
    }
}
