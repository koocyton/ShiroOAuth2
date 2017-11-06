package com.doopp.gauss.api.entity;

import com.doopp.gauss.server.task.GameTask;
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
    public enum GameStatus {
        Resting,
        Calling,
        Playing
    }

    // 游戏类型
    public final static int NULL_GAME = 0;
    public final static int WERE_WOLF_GAME = 1;
    public final static int BATTLE_ROYALE_GAME = 2;
    public final static int GUESS_DRAW_GAME = 3;

    // 房间 ID
    @Getter
    @Setter
    private int id;

    // 房间有多少个座位，即最多坐多少人
    @Getter private int seatCount = 100;

    // 房间名
    @Getter @Setter private String name;

    // 房主
    @Getter private UserEntity owner;

    // 围观玩家
    @Getter private Map<Long, UserEntity> watchUsers = new HashMap<>();

    // 游戏玩家
    @Getter private Map<Long, Long> gameUsersId = new HashMap<>();

    // 游戏状态
    @Getter @Setter private RoomEntity.GameStatus gameStatus = GameStatus.Resting;

    // 游戏类型
    @Getter @Setter private GameTask gameTask = null;

    // 游戏名
    @Getter @Setter private int gameType = NULL_GAME;

    // 按 ID 获取用户
    public UserEntity getGameUser(Long userId) {
        Long gameUserId = this.gameUsersId.get(userId);
        if (gameUserId==null) {
            return null;
        }
        UserEntity oneWatchUser = this.watchUsers.get(gameUserId);
        if (oneWatchUser!=null) {
            return oneWatchUser;
        }
        else if (this.getOwner()!=null && this.getOwner().getId().equals(userId)) {
            return this.getOwner();
        }
        return null;
    }

    // 获取所有房间内的游戏用户
    public Map<Long, UserEntity> getGameUsers() {
        Map<Long, UserEntity> gameUsers = new HashMap<>();
        for(Long userId : gameUsersId.values()) {
            if (this.getOwner()!=null && this.getOwner().getId().equals(userId)) {
                gameUsers.put(userId, this.getOwner());
            }
            else if (this.watchUsers.get(userId)!=null) {
                gameUsers.put(userId, this.watchUsers.get(userId));
            }
        }
        return gameUsers;
    }

    // 获取所有房间内的游戏用户 ID
    public Map<Long, Long> getGameUsersId() {
        return gameUsersId;
    }

    // 加入到房主
    public void setOwner(UserEntity user) {
        if (this.getOwner()==null) {
            this.watchUsers.remove(user.getId());
            this.owner = user;
        }
    }

    // 加入到围观玩家
    public void joinWatch(UserEntity user) {
        this.watchUsers.put(user.getId(), user);
    }

    // 离开房间
    public void userLeave(UserEntity user) {
        if (this.owner!=null && Objects.equal(this.owner.getId(), user.getId())) {
            this.owner = null;
        }
        this.watchUsers.remove(user.getId());
        this.gameUsersId.remove(user.getId());
    }

    // 参加活动的人数
    public int playerNumber() {
        return this.gameUsersId.size();
    }

    // 加入活动
    public void joinGame(UserEntity user) {
        this.gameUsersId.put(user.getId(), user.getId());
    }

    // 离开活动
    public void leaveGame(UserEntity user) {
        this.gameUsersId.remove(user.getId());
    }

    // 重置
    public void resetGame() {
        this.gameUsersId = new HashMap<>();
        this.gameStatus = GameStatus.Resting;
        this.gameType = NULL_GAME;
    }
}
