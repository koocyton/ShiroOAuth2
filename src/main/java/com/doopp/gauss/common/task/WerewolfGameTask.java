package com.doopp.gauss.common.task;

import com.doopp.gauss.common.defined.Action;
import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.service.GameService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WerewolfGameTask implements Runnable {

    private final Identity[] t9Identity = {
            Identity.WOLF, Identity.WOLF, Identity.WOLF,
            Identity.VILLAGER, Identity.VILLAGER, Identity.VILLAGER,
            Identity.SEER, Identity.WITCH, Identity.HUNTER
    };

    private final Identity[] t12Identity = {
            Identity.WOLF, Identity.WOLF, Identity.WOLF, Identity.WOLF,
            Identity.VILLAGER, Identity.VILLAGER, Identity.VILLAGER, Identity.VILLAGER,
            Identity.SEER, Identity.WITCH, Identity.HUNTER
    };

    // logger
    private final static Logger logger = LoggerFactory.getLogger(WerewolfGameTask.class);

    private final static GameService gameService = (GameService) ApplicationContextUtil.getBean("gameService");

    // private final static PlayerDao playerDao = (PlayerDao) ApplicationContextUtil.getBean("playerDao");

    // private final static RoomDao roomDao = (RoomDao) ApplicationContextUtil.getBean("roomDao");

    // 这个线程处理的房间
    private final Room room;

    public WerewolfGameTask (Room room) {
        this.room = room;
    }

    @Override
    public void run() {
        this.callGameStart();
    }

    // 获得一个随机的身份序列
    private Identity[] getRandomIdentities() {
        return (room.getGameLevel()==0) ? this.t9Identity.clone() : this.t12Identity.clone();
    }

    // 所有游戏准备好了后，游戏开始
    private void callGameStart() {
        gameService.sendMessage(room, Action.GAME_START, null);
        this.waitPlayerAction(3, Action.ONLY_WAIT);
        this.distributeIdentity();
    }

    // 先随机派发用户身份
    private void distributeIdentity() {
        Identity[] identities = this.getRandomIdentities();
        Player[] players = room.getSeats();
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
            gameService.sendMessage(players[ii], Action.PLAYER_IDENTITY, players[ii].getIdentity());
        }
        Player[] wolfs = room.getWolfs();
        // 通知狼的身份
        for(Player wolf : wolfs) {
            gameService.sendMessage(wolf, Action.WOLF_IDENTITY, room.getWolfSeat());
        }
        // 进入夜晚
        this.enterNight();
    }

    // 下发，进入夜晚
    private void enterNight() {
        // 预言家行动
        this.callSeer();
        // 狼人行动
        this.callWolf();
        // 女巫开始行动
        // this.callWitch();
        // 女巫操作完毕进入白天
        // this.enterDay(room);
    }

    // 预言家查身份
    private void callSeer() {
        gameService.sendMessage(room.getSeer(), Action.SEER_CALL, null);
    }

    // 狼人开始杀人，预言家查身份
    private void callWolf() {
        gameService.sendMessage(room.getWolfs(), Action.WOLF_CALL, null);
        // 等待 40 秒狼操作完毕
        this.waitPlayerAction(5, Action.WOLF_CHOICE);
        // 查询投票最多的玩家
        // Player mostTargetPlayer = roomDao.mostTargetPlayer(room, Action.WOLF_CHOICE);
        // 记录到 action
        // roomDao.cacheAction(Action.WOLF_KILL, null, mostTargetPlayer);
    }

//    // 下发，女巫救人或毒杀
//    private void callWitch() {
//        Player witch = playerDao.getWitchByRoom(room);
//        // 如果女巫活着
//        if (witch.isLiving()) {
//            // 检查狼杀的目标
//            Player wolfKillPlayer = roomDao.mostTargetPlayer(room, Action.WOLF_CHOICE);
//            gameService.sendMessage(playerDao.getWitchByRoom(room), Action.WITCH_CALL, wolfKillPlayer);
//            // 等待女巫操作完毕
//            this.waitPlayerAction(20, room, Action.WITCH_CHOICE);
//        }
//    }

//    // 下发，猎人杀人
//    private void callHunter(Room room) {
//        Player hunter = playerDao.getHunterByRoom(room);
//        if (hunter.isLiving()) {
//            gameService.sendMessage(hunter, Action.HUNTER_CALL, null);
//            // 等待猎人操作完毕
//            this.waitPlayerAction(20, room, Action.HUNTER_CHOICE);
//        }
//        // 检查猎人的目标
//        Player hunterKillPlayer = roomDao.mostTargetPlayer(room, Action.HUNTER_CHOICE);
//        // 谁被杀了
//        gameService.sendMessage(room, Action.SHOW_PLAYER_DIE, new Long[]{hunterKillPlayer.getId()});
//    }
//
//    // 下发，进入白天
//    private void enterDay(Room room) {
//        // 统计晚上的杀人情况
//        this.summaryNightResult(room);
//        // 如果胜利
//        if (this.checkVictory(room)) {
//            gameService.sendMessage(room, Action.SHOW_RESULT, null);
//            return;
//        }
//        // 玩家发言
//        this.callAllPlayerSpeak(room);
//        // 玩家投票
//        this.callAllPlayerVote(room);
//        // 统白天的杀人情况
//        this.summaryDayResult(room);
//        // 如果胜利
//        if (this.checkVictory(room)) {
//            gameService.sendMessage(room, Action.SHOW_RESULT, null);
//            return;
//        }
//        // 清空 action
//        room.flushCacheAction();
//        // 进入夜晚
//        this.enterNight(room);
//    }
//
//    // 汇总夜晚的结果
//    private void summaryNightResult(Room room) {
//        // 检查狼杀的目标
//        Player wolfKillPlayer = roomDao.mostTargetPlayer(room, Action.WOLF_CHOICE);
//        // 检查女巫执行的目标
//        Player witchKillPlayer = roomDao.mostTargetPlayer(room, Action.WOLF_CHOICE);
//
//        // 如果，都没操作
//        if (wolfKillPlayer==null && witchKillPlayer==null) {
//            // 平安夜
//            gameService.sendMessage(playerDao.getWitchByRoom(room), Action.SHOW_SAFE_NIGHT, null);
//        }
//        // 狼没杀人，女巫有毒杀
//        else if (wolfKillPlayer==null) {// && witchKillPlayer!=null
//            // 有人挂了
//            gameService.sendMessage(playerDao.getWitchByRoom(room), Action.SHOW_PLAYER_DIE, new Long[]{witchKillPlayer.getId()} );
//            witchKillPlayer.setStatus(0);
//            // 如果是猎人
//            if (witchKillPlayer.getIdentity()==Identity.HUNTER) {
//                this.callHunter(room);
//            }
//        }
//        // 狼杀人，女巫没有救
//        else if (witchKillPlayer==null) { // && wolfKillPlayer!=null
//            // 有人挂了
//            gameService.sendMessage(playerDao.getWitchByRoom(room), Action.SHOW_PLAYER_DIE, new Long[]{wolfKillPlayer.getId()} );
//            wolfKillPlayer.setStatus(0);
//            // 如果是猎人
//            if (wolfKillPlayer.getIdentity()==Identity.HUNTER) {
//                this.callHunter(room);
//            }
//        }
//        // 如果同一个人，就是狼杀，女巫救
//        else if (wolfKillPlayer.getId().equals(witchKillPlayer.getId())) {
//            // 平安夜
//            gameService.sendMessage(playerDao.getWitchByRoom(room), Action.SHOW_SAFE_NIGHT, null);
//        }
//        // 如果不是同一个人，狼杀了一个，女巫杀了一个
//        else {
//            // 挂了两个
//            gameService.sendMessage(playerDao.getWitchByRoom(room), Action.SHOW_PLAYER_DIE, new Long[]{witchKillPlayer.getId(),wolfKillPlayer.getId()} );
//            witchKillPlayer.setStatus(0);
//            wolfKillPlayer.setStatus(0);
//            if (wolfKillPlayer.getIdentity()==Identity.HUNTER || witchKillPlayer.getIdentity()==Identity.HUNTER) {
//                this.callHunter(room);
//            }
//        }
//    }
//
//    // 汇总白天的结果
//    private void summaryDayResult(Room room) {
//        // 检查票杀的目标
//        Player voteKillPlayer = roomDao.mostTargetPlayer(room, Action.PLAYER_VOTE);
//        // 谁被杀了
//        gameService.sendMessage(room, Action.SHOW_PLAYER_DIE, new Long[]{voteKillPlayer.getId()});
//        // 如果票猎人，出发猎人动作
//        if (voteKillPlayer.getIdentity()==Identity.HUNTER) {
//            this.callHunter(room);
//        }
//    }
//
//    // 所有存活玩家发言
//    private void callAllPlayerSpeak(Room room) {
//        for(Player player : room.getPlayers()) {
//            if (player.isLiving()) {
//                gameService.sendMessage(player, Action.PLAYER_SPEAK, null);
//                this.waitPlayerAction(10, room, Action.PLAYER_SPEAK);
//            }
//        }
//    }
//
//    // 所有存活玩家投票
//    private void callAllPlayerVote(Room room) {
//        gameService.sendMessage(room, Action.PLAYER_VOTE, null);
//        this.waitPlayerAction(30, room, Action.PLAYER_VOTE);
//    }
//
//    // 检查胜利
//    private boolean checkVictory(Room room) {
//        Player[] wolfs = playerDao.getWolfsByRoom(room);
//        if (wolfs.length==0) {
//            return false;
//        }
//        return true;
//    }

    private void waitPlayerAction(int second, String action) {
        try {
            synchronized (this) {
                room.setAcceptAction(action);
                this.wait(second * 1000);
                room.setAcceptAction(Action.ONLY_WAIT);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
