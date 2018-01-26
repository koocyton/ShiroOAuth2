package com.doopp.gauss.common.entity;

import com.doopp.gauss.common.task.WerewolfGameTask;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * 房间的实体
 *
 * Created by Henry on 2017/10/26.
 */
@Data
public class Room {


    private final static Logger logger = LoggerFactory.getLogger(Room.class);

    // 房间 ID
    private int id;

    // 房间的用户
    private Player[] players = {
        null, null, null, null, null, null,
        null, null, null, null, null, null,
    };

    // 记录每个阶段的结果
    private String[] recordRound = new String[] {};

    // 当前阶段游戏的步骤
    private Map<Long, PlayerAction> cacheActions = new HashMap<>();

    // 预言家的座位
    private int seerSeat;

    // 女巫的座位
    private int witchSeat;

    // 丘比特的座位
    private int cupidSeat;

    // 猎人的座位
    private int hunterSeat;

    // 村民的座位
    private int[] villagerSeat;

    // 狼人的座位
    private int[] wolfSeat;

    // 房间的状态     0:准备中<等待开局>    1:游戏进行中<已开局>
    private int status = 0;

    // 房间游戏开局的类型      0:普通(normal)    1:高阶(high)
    private int gameLevel = 0;

    // 房间里的游戏
    private WerewolfGameTask gameTask;

    // 进行的队列指定等待用户的上发的类型
    private String waitAction;

    public void addVillagerSeat(int seatIndex) {
        if (this.villagerSeat==null) {
            return;
        }
        int ii = this.villagerSeat.length;
        this.villagerSeat[ii] = seatIndex;
    }

    public void addWolfSeat(int seatIndex) {
        if (this.wolfSeat==null) {
            return;
        }
        int ii = this.wolfSeat.length;
        this.wolfSeat[ii] = seatIndex;
    }

    public void setCacheAction(PlayerAction playerAction) {
        this.cacheActions.put(playerAction.getActionPlayer().getId(), playerAction);
    }

    public Map<Long, PlayerAction> getCacheActions(String action) {
        Map<Long, PlayerAction> actions = new HashMap<>();
        for(Long key : this.cacheActions.keySet()) {
            PlayerAction playerAction = this.cacheActions.get(key);
            if (playerAction.getAction().equals(action)) {
                actions.put(playerAction.getActionPlayer().getId(), playerAction);
            }
        }
        return actions;
    }

    public int cacheActionCount(String action) {
        Map<Long, PlayerAction> actions = this.getCacheActions(action);
        return actions==null ? 0 : actions.size();
    }

    public void flushCacheAction() {
        this.cacheActions = new HashMap<>();
    }
}
