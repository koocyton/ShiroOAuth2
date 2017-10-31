package com.doopp.gauss.api.entity;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 房间的实体
 *
 * Created by Henry on 2017/10/26.
 */
public class RoomEntity {

    // 游戏状态
    private enum GameStatus {
        Resting,
        Calling,
        Playing
    }

    // 房间 ID
    @Getter
    @Setter
    private int id;

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

    // 游戏玩家
    @Getter private Map<Long, UserEntity> gameUsers = new HashMap<>();

    // 游戏状态
    @Getter private RoomEntity.GameStatus gameStatus = GameStatus.Resting;

    // 游戏类型
    @Getter private String gameType = "";

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

    // 参加活动的人数
    public int playerNumber() {
        return this.gameUsers.size();
    }

    // 加入活动
    public void joinGame(UserEntity user) {
        this.gameUsers.put(user.getId(), user);
    }

    // 重置
    public void resetGame() {
        this.gameUsers = new HashMap<>();
        this.gameStatus = GameStatus.Resting;
        this.gameType = "";
    }
}
