package com.doopp.gauss.common.task;

import com.doopp.gauss.common.dao.PlayerDao;
import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.PlayerAction;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.PlayService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import com.doopp.gauss.common.utils.CommonUtils;
import com.doopp.gauss.server.configuration.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WerewolfGameTask implements Runnable {

    private final Identity[] identities = {
        Identity.WOLF, Identity.WOLF, Identity.WOLF,
        Identity.VILLAGER, Identity.VILLAGER, Identity.VILLAGER,
        Identity.SEER, Identity.WITCH, Identity.HUNTER
    };

    // logger
    private final static Logger logger = LoggerFactory.getLogger(WerewolfGameTask.class);

    private final static PlayService playService = (PlayService) ApplicationContextUtil.getBean("playService");

    private final static PlayerDao playerDao = (PlayerDao) ApplicationContextUtil.getBean("playerDao");

    // 这个线程处理的房间
    private final Room room;

    public WerewolfGameTask (Room room) {
        this.room = room;
    }

    public void run() {
        this.callGameStart(room);
    }

    // 所有游戏准备好了后，游戏开始
    private void callGameStart(Room room) {
        playService.sendMessage(room, "{\"action\":\"game-start\"}");
        playService.delay(5);
        this.distributeIdentity(room);
    }

    // 先随机派发用户身份
    private void distributeIdentity(Room room) {
        Identity[] identities = this.getRandomIdentities();
        Player[] players = room.getPlayers();
        for (int ii=0; ii<identities.length; ii++) {
            if (players[ii]==null) {
                continue;
            }
            // 呃，好吧
            switch (identities[ii]) {
                case SEER :
                    room.setSeerSeat(ii);
                    break;
                case HUNTER :
                    room.setHunterSeat(ii);
                    break;
                case WITCH :
                    room.setWitchSeat(ii);
                    break;
                case CUPID :
                    room.setCupidSeat(ii);
                    break;
                case VILLAGER :
                    room.addVillagerSeat(ii);
                case WOLF :
                    room.addWolfSeat(ii);
                    break;
            }
            // 设置身份
            players[ii].setIdentity(identities[ii]);
            // 通知玩家自己身份
            playService.sendMessage(players[ii], "distribute-identity", players[ii].getIdentity());
        }
        Player[] wolfs = playerDao.getWolfsByRoom(room);
        // 通知狼的身份
        for(Player wolf : wolfs) {
            playService.sendMessage(wolf, "wolf-identity", room.getWolfSeat());
        }
        // 进入夜晚
        this.enterNight(room);
    }

    // 获得一个随机的身份序列
    private Identity[] getRandomIdentities() {
        // Identity[] identities = this.identities.clone();
        return this.identities.clone();
    }

    // 下发，进入夜晚
    private void enterNight(Room room) {
        this.callSeer(room);
    }

    // 预言家查身份
    private void callSeer(Room room) {
        playService.sendMessage(playerDao.getSeerByRoom(room), "call-seer", null);
        this.callWolf(room);
    }

    // 狼人开始杀人，预言家查身份
    private void callWolf(Room room) {
        playService.sendMessage(playerDao.getWolfsByRoom(room), "call-wolf", null);

        synchronized (room) {
            try {
                this.wait(40);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 初始化投票结果
        Map<Long, Integer> votes = new HashMap<>();
        // 检查狼人是否执行完毕
        while(true) {
            // 初始化投票结果
            votes = new HashMap<>();
            playService.delay(1);
            // 如果存活的狼操作完毕
            Player[] wolfs = playerDao.getWolfsByRoom(room);
            Map<Long, PlayerAction> cacheActions = room.getCacheActions();
            boolean allActioned = true;
            // 检查
            for(Player wolf : wolfs) {
                // 数据完整检查，不能是不活动的狼
                if (wolf!=null && wolf.isLiving()) {
                    // 从缓存里拿这个狼的 action
                    PlayerAction playerAction = cacheActions.get(wolf.getId());
                    // 如果没有提交，就标注没有全部完成 action ，继续等待
                    if (playerAction==null) {
                        allActioned = false;
                    }
                    // 有提交，就将投票汇总到 map 里，用被杀的用户ID做索引
                    else {
                        // 取得被杀用户的投票数
                        Integer vote = votes.get(playerAction.getTargetPlayer().getId());
                        // 投票数 +1
                        votes.put(wolf.getId(), (vote==null) ? 1 : vote + 1);
                    }
                }
            }
            // 如果都处理完了
            if (allActioned) {
                break;
            }
        }
        // 汇总票数狼人杀人
        Long killPlayerId = CommonUtils.getMaxValueKey(votes);
        // 女巫开始行动
        this.callWitch(room, playerDao.getPlayerById(killPlayerId));
    }

    // 下发，女巫救人或毒杀
    private void callWitch(Room room, Player wolfKillPlayer) {
        // 检查狼人杀人
        if (wolfKillPlayer!=null) {
            playService.sendMessage(playerDao.getWolfsByRoom(room), "call-witch", new HashMap<String, Long>(){{
                put("wolf-kill-player", wolfKillPlayer.getId());
            }});
        }
        else {
            playService.sendMessage(playerDao.getWolfsByRoom(room), "call-witch", null);
        }
        // 检查女巫是否执行完毕
        while(true) {
            playService.delay(1);
            boolean isActioned = true;
            Player witch = playerDao.getWitchByRoom(room);
            Map<Long, PlayerAction> cacheActions = room.getCacheActions();
            // 如果女巫存活，但没有提交 action
            if (witch.isLiving() && cacheActions.get(witch.getId())==null) {
                isActioned = false;
            }
            if (isActioned) {
                break;
            }
        }
        // 女巫操作完毕进入白天
        this.enterDay(room);
    }

    // 下发，猎人杀人
    private void callHunter(Room room) {
        // 检查猎人是否被杀
        Map<Long, PlayerAction> cacheActions = room.getCacheActions();
        playService.sendMessage(playerDao.getHunterByRoom(room), "call-witch", null);
    }

    // 下发，进入白天
    private void enterDay(Room room) {
        this.sendLastNightResult(room);
        this.callHunter(room);
        if (this.checkVictory()) {
            this.sendResults(room, "");
            return;
        }
        this.callAllSpeak(room);
        this.callVoter(room);
        this.callHunter(room);
        if (this.checkVictory()) {
            this.sendResults(room, "");
            return;
        }
        enterNight(room);
    }

    // 发送昨晚的结果
    private void sendLastNightResult(Room room) {

    }

    // 下发，结果
    private void sendResults(Room room, String message) {

    }

    // 下发，游戏结束
    private void callGameOver(Room room) {

    }

    // 检查胜利
    private boolean checkVictory() {
        return true;
    }

    private void callAllSpeak(Room room) {
        for(Player player : room.getPlayers()) {
            if (player.isLiving()) {
                playService.sendMessage(player, "{\"action\":\"speak\", \"user\":"+player.getId()+", \"timeLimit\":30}");
                playService.delay(30);
            }
        }
    }

    private void callVoter(Room room) {
    }
}
