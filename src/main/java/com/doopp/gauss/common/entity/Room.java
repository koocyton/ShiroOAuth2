package com.doopp.gauss.common.entity;

import com.doopp.gauss.common.defined.PlayerStatus;
import com.doopp.gauss.common.task.WerewolfGameTask;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房间的实体
 *
 * Created by Henry on 2017/10/26.
 */
public class Room {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(Room.class);

    // 房间 ID
    @Getter @Setter
    private int id;

    // 记录每个阶段的结果
    @Getter
    private String[] recordRound = new String[] {};

    // 当前阶段游戏的步骤
    private Map<Long, PlayerAction> cacheActions = new HashMap<>();

    // 房间内的玩家
    @Getter
    private Player[] seats = {
        null, null, null, null, null, null,
        null, null, null, null, null, null,
    };

    // 预言家的座位
    @Setter
    private Integer seerSeat = null;

    // 女巫的座位
    @Setter
    private Integer witchSeat = null;

    // 丘比特的座位
    @Setter
    private Integer cupidSeat = null;

    // 猎人的座位
    @Setter
    private Integer hunterSeat = null;

    // 村民的座位
    private List<Integer> villagerSeats = new ArrayList<>();

    // 狼人的座位
    private List<Integer> wolfSeats = new ArrayList<>();

    // 房间的状态     0:准备中<等待开局>    1:游戏进行中<已开局>
    @Getter @Setter
    private int status = 0;

    // 房间游戏开局的类型      0:普通(normal)    1:高阶(high)
    @Getter @Setter
    private int gameLevel = 0;

    // 进行的队列指定等待用户的上发的类型
    @Getter @Setter
    private String acceptAction;

    // 操作房间内游戏的线程
    @Getter @Setter
    private WerewolfGameTask gameTask;

    public void gameContinue() {
        this.gameTask.notify();
    }

    public boolean allReady() {
        int maxLength = (this.gameLevel==1) ? 12 : 9;
        for(int ii=0; ii<maxLength; ii++) {
            if (seats[ii]==null || seats[ii].getStatus().equals(PlayerStatus.WAITING)) {
                return false;
            }
        }
        return true;
    }

    public int playerCount() {
        int maxLength = (this.gameLevel==1) ? 12 : 9;
        int count = 0;
        for(int ii=0; ii<maxLength; ii++) {
            if (seats[ii]!=null) {
                count++;
            }
        }
        return count;
    }

    public synchronized boolean noSeat() {
        // 按游戏等级区分能进入多少个用户
        int maxLength = (this.gameLevel==1) ? 12 : 9;
        //
        for(int ii=0; ii<maxLength; ii++) {
            if (seats[ii]==null) {
                return false;
            }
        }
        //
        return true;
    }

    // 加入一个用户
    public boolean addPlayer(Player joinPlayer) {
        if (!noSeat()) {
            for (int ii = 0; ii < seats.length; ii++) {
                if (seats[ii] == null) {
                    seats[ii] = joinPlayer;
                    seats[ii].setRoom_id(this.getId());
                    return true;
                }
            }
        }
        return false;
    }

    // 减去一个用户
    public void delPlayer(Player leavePlayer) {
        if (leavePlayer!=null) {
            for (int ii = 0; ii < seats.length; ii++) {
                if (seats[ii] != null) {
                    if (seats[ii].getId().equals(leavePlayer.getId())) {
                        seats[ii] = null;
                    }
                }
            }
        }
    }

    public void addCacheAction(PlayerAction playerAction) {
        this.cacheActions.put(playerAction.getActionPlayer(), playerAction);
    }

    public Map<Long, PlayerAction> getCacheActions(String action) {
        Map<Long, PlayerAction> actions = new HashMap<>();
        for(Long key : this.cacheActions.keySet()) {
            PlayerAction playerAction = this.cacheActions.get(key);
            if (playerAction.getAction().equals(action)) {
                actions.put(playerAction.getActionPlayer(), playerAction);
            }
        }
        return actions;
    }

    public int countCacheAction(String action) {
        Map<Long, PlayerAction> actions = this.getCacheActions(action);
        return actions==null ? 0 : actions.size();
    }

    public void resetCacheAction() {
        this.cacheActions = new HashMap<>();
    }

    // 加入狼
    public void addWolfSeat(int seatIndex) {
        int ii = this.wolfSeats.size();
        this.wolfSeats.add(ii, seatIndex);
    }

    // 获取房间里的狼
    public Player[] getWolfs() {
        Player[] wolfs = (this.gameLevel==1) ? new Player[]{null, null, null, null} : new Player[]{null, null, null};
        for(int ii=0; ii<wolfs.length; ii++) {
            wolfs[ii] = this.seats[this.wolfSeats.get(ii)];
        }
        return wolfs;
    }

    // 加入村民
    public void addVillagerSeat(int seatIndex) {
        int ii = this.villagerSeats.size();
        this.villagerSeats.add(ii, seatIndex);
    }

    // 获取房间里的村民
    public Player[] getVillagers() {
        Player[] villagers = (this.gameLevel==1) ? new Player[]{null, null, null, null} : new Player[]{null, null, null};
        for(int ii=0; ii<villagers.length; ii++) {
            villagers[ii] = this.seats[this.villagerSeats.get(ii)];
        }
        return villagers;
    }

    // 获取房间里的先知
    public Player getSeer() {
        return this.seats[this.seerSeat];
    }

    // 获取房间里的猎人
    public Player getHunter() {
        return this.seats[this.hunterSeat];
    }

    // 获取房间里的女巫
    public Player getWitch() {
        return this.seats[this.witchSeat];
    }

    // 获取房间里的丘比特
    public Player getCupid() {
        return this.seats[this.cupidSeat];
    }
}
