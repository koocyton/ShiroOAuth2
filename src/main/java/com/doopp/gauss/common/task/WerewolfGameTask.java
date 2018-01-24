package com.doopp.gauss.common.task;

import com.doopp.gauss.common.dao.PlayerDao;
import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.PlayerAction;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.PlayService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        this.callWolfSeer(room);
    }

    // 狼人开始杀人，预言家查身份
    private void callWolfSeer(Room room) {
        playService.sendMessage(playerDao.getWolfsByRoom(room), "call-wolf", null);
        playService.sendMessage(playerDao.getSeerByRoom(room), "call-seer", null);
        // 检查狼人是否执行完毕
        while(true) {
            playService.delay(1);
            // 如果存活的狼操作完毕
            Player[] wolfs = playerDao.getWolfsByRoom(room);
            Map<Long, PlayerAction> cacheActions = room.getCacheActions();
            boolean allActioned = true;
            for(Player wolf : wolfs) {
                if (wolf!=null && wolf.isLiving()) {
                    if (cacheActions.get(wolf.getId())==null) {
                        allActioned = false;
                    }
                }
            }
            if (allActioned) {
                break;
            }
        }
        // 女巫开始行动
        this.callWitch(room);
    }

    // 下发，女巫救人或毒杀
    private void callWitch(Room room) {
        // 检查狼人杀人
        playService.sendMessage(playerDao.getWolfsByRoom(room), "call-witch", null);
        // 检查狼人是否执行完毕
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
    }

    // 处理结果

    // 下发，猎人杀人
    private void callHunter(Room room) {
        // 检查猎人是否被杀
        Map<Long, PlayerAction> cacheActions = room.getCacheActions();
        playService.sendMessage(playerDao.getHunterByRoom(room), "call-witch", null);
    }

    // 下发，进入白天
//    private void enterDay(Room room) {
//        this.sendLastNightResult(room);
//        this.callHunter(room);
//        if (this.checkVictory()) {
//            this.sendResults(room, "");
//            return;
//        }
//        this.callAllSpeak(room);
//        this.callVoter(room);
//        this.callHunter(room);
//        if (this.checkVictory()) {
//            this.sendResults(room, "");
//            return;
//        }
//        enterNight(room);
//    }

    // 发送昨晚的结果
//    private void sendLastNightResult(Room room) {
//
//    }

    // 下发，狼人出来杀人
//    private void callWolf(Room room) {
//        User[] users = room.getUsers();
//        User[] wolfs = new User[]{};
//        int ii = 0;
//        // 拿到谁是狼
//        for(User user : users) {
//            if (user!=null && user.getIdentity().equals(WOF_ID)) {
//                wolfs[ii++] = user;
//            }
//        }
//        // 狼开始行动
//        for(User user : wolfs) {
//            playService.sendMessage(user, "{\"action\":\"wolf-killings\"");
//        }
//        int maxLoopNumber = 40;
//        int nowLoopNumber = 0;
//        // 遍历检擦狼是否执行完成
//        while(true) {
//            playService.delay(1);
//            boolean allAction = true;
//            for(User user : wolfs) {
//                if (user.getAction == false) {
//                    allAction = false;
//                }
//            }
//            nowLoopNumber++;
//            if (allAction || nowLoopNumber>maxLoopNumber) {
//                this.callWitch(room);
//            }
//        }
//    }
//
//    // 下发，预言家查身份
//    private void callSeer(Room room) {
//        playService.delay(40);
//    }
//
//    // 下发，女巫救人或毒杀
//    private void callWitch(Room room) {
//        playService.delay(40);
//    }
//
//    // 下发，猎人杀人
//    private void callHunter(Room room) {
//        playService.delay(40);
//    }
//
//
//
//    // 下发，结果
//    private void sendResults(Room room, String message) {
//
//    }
//
//    // 下发，游戏结束
//    private void callGameOver(Room room) {
//
//    }
//
//    // 检查胜利
//    private boolean checkVictory() {
//        return true;
//    }
//
//    private void callAllSpeak(Room room) {
//        for(User user : room.getUsers()) {
//            if (user.isLiving()) {
//                this.callOneSpeak(user);
//                playService.delay(30);
//            }
//        }
//    }
//
//    private void callVoter(Room room) {
//    }
//
//    private void callOneSpeak(User user) {
//        playService.sendMessage(user, "{\"action\":\"speak\", \"user\":"+user.getId()+", \"timeLimit\":30}");
//    }
}
