package com.doopp.gauss.common.task;

import com.doopp.gauss.common.dao.PlayerDao;
import com.doopp.gauss.common.dao.RoomDao;
import com.doopp.gauss.common.defined.Action;
import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.PlayerAction;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.service.PlayService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import com.doopp.gauss.common.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

    private final static RoomDao roomDao = (RoomDao) ApplicationContextUtil.getBean("roomDao");

    // 这个线程处理的房间
    private final Room room;

    public WerewolfGameTask (Room room) {
        this.room = room;
    }

    // 获得一个随机的身份序列
    private Identity[] getRandomIdentities() {
        // Identity[] identities = this.identities.clone();
        return this.identities.clone();
    }

    public void run() {
        this.callGameStart(room);
    }

    // 所有游戏准备好了后，游戏开始
    private void callGameStart(Room room) {
        playService.sendMessage(room, "{\"action\":\"game-start\"}");
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

    // 下发，进入夜晚
    private void enterNight(Room room) {
        this.callSeer(room);
    }

    // 预言家查身份
    private void callSeer(Room room) {
        playService.sendMessage(playerDao.getSeerByRoom(room), Action.SEER_CALL, null);
        this.callWolf(room);
    }

    // 狼人开始杀人，预言家查身份
    private void callWolf(Room room) {
        playService.sendMessage(playerDao.getWolfsByRoom(room), Action.WOLF_CALL, null);
        // 等待 40 秒狼操作完毕
        this.waitPlayerAction(40, room, Action.WOLF_CHOICE);
        // 查询投票最多的玩家
        Player mostTargetPlayer = roomDao.mostTargetPlayer(room, Action.WOLF_CHOICE);
        // 记录到 action
        roomDao.cacheAction(Action.WOLF_KILL, null, mostTargetPlayer);
        // 女巫开始行动
        this.callWitch(room);
    }

    // 下发，女巫救人或毒杀
    private void callWitch(Room room) {
        Player witch = playerDao.getWitchByRoom(room);
        // 如果女巫活着
        if (witch.isLiving()) {
            // 检查狼杀的目标
            Map<Long, PlayerAction> wolfKill = room.getCacheActions(Action.WOLF_KILL);
            if (wolfKill!=null) {
                playService.sendMessage(playerDao.getWitchByRoom(room), Action.WITCH_CALL, null);
            }
            else {
                playService.sendMessage(playerDao.getWitchByRoom(room), Action.WITCH_CALL, null);
            }
            // 等待女巫操作完毕
            this.waitPlayerAction(20, room, Action.WITCH_CHOICE);
            // 查询投票最多的玩家
            Player mostTargetPlayer = roomDao.mostTargetPlayer(room, Action.WITCH_CHOICE);
        }
        // 女巫操作完毕进入白天
        this.enterDay(room);
    }

    // 下发，进入白天
    private void enterDay(Room room) {
        this.summaryNightResult(room);
        // 如果胜利
        if (this.checkVictory()) {
            this.sendResults(room, "");
            return;
        }
        // 如果昨晚有杀猎人
        this.callHunter(room);
    }

    // 汇总夜晚的结果
    private void summaryNightResult(Room room) {
        Map<Long, PlayerAction> wolfActions = room.getCacheActions(Action.SEER_CALL);
        Long bbbbb = CommonUtils::getMaxValueKey(wolfActions);

        // 狼人杀了谁 a
        // 女巫杀了谁 b
        // 女巫救了谁 c
        // !a !c !b : 平安夜
        // c && a==c : 平安夜
        // !c && a :  a 死
        // b  : b 死
    }

    // 下发，猎人杀人
    private void callHunter(Room room) {
        Player hunter = playerDao.getHunterByRoom(room);
        if (hunter.isLiving()) {
            playService.sendMessage(playerDao.getWitchByRoom(room), "call-witch", null);
            // 等待女巫操作完毕
            this.waitPlayerAction(20);
        }
        // 检查猎人是否被杀
        Map<Long, PlayerAction> cacheActions = room.getCacheActions();
        playService.sendMessage(playerDao.getHunterByRoom(room), "call-witch", null);
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

    private void waitPlayerAction(int second, Room room, String action) {
        try {
            room.setWaitAction(action);
            this.wait(second * 1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
