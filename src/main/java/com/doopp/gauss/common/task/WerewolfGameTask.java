package com.doopp.gauss.common.task;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.defined.Action;
import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.PlayerAction;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.service.GameService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


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

    // 这个房间
    private final Room room;

    public WerewolfGameTask(Room room) {
        this.room = room;
        this.room.setGameTask(this);
    }

    @Override
    public void run() {
        this.callGameStart();
    }

    // 获得一个随机的身份序列
    private Identity[] getRandomIdentities() {
        return (room.getGameLevel() == 0) ? this.t9Identity : this.t12Identity;
    }

    // 所有游戏准备好了后，游戏开始
    private void callGameStart() {
        this.waitPlayerAction(3, Action.REJECT_ACTION);
        gameService.sendMessage(room, Action.GAME_START, null);
        this.waitPlayerAction(3, Action.REJECT_ACTION);
        this.distributeIdentity();
    }

    // 先随机派发用户身份
    private void distributeIdentity() {
        Identity[] identities = this.getRandomIdentities();
        Player[] players = room.getSeats();
        for (int ii = 0; ii < identities.length; ii++) {
            if (players[ii] == null) {
                continue;
            }
            // 呃，好吧
            switch (identities[ii]) {
                case SEER:
                    room.setSeerSeat(ii);
                    break;
                case HUNTER:
                    room.setHunterSeat(ii);
                    break;
                case WITCH:
                    room.setWitchSeat(ii);
                    break;
                case CUPID:
                    room.setCupidSeat(ii);
                    break;
                case VILLAGER:
                    room.addVillagerSeat(ii);
                case WOLF:
                    room.addWolfSeat(ii);
                    break;
            }
            // 设置身份
            players[ii].setIdentity(identities[ii]);
            // 通知玩家自己身份
            gameService.sendMessage(players[ii], Action.PLAYER_IDENTITY, players[ii].getIdentity());
        }
        // 通知狼好友
        gameService.sendMessage(room.getWolfs(), Action.WOLF_IDENTITY, room.getWolfs());
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
        this.callWitch();
        // 女巫操作完毕进入白天
        this.enterDay();
    }

    // 预言家查身份
    private void callSeer() {
        JSONObject jsonObject = new JSONObject(){{
            put("wait-time", 60);
        }};
        gameService.sendMessage(room.getSeer(), Action.SEER_CALL, jsonObject);
    }

    // 狼人开始杀人，预言家查身份
    private void callWolf() {
        JSONObject jsonObject = new JSONObject(){{
            put("wait-time", 60);
        }};
        gameService.sendMessage(room.getWolfs(), Action.WOLF_CALL, jsonObject);
        // 等待 40 秒狼操作完毕
        this.waitPlayerAction(60, Action.WOLF_CHOICE);
        // 查询投票最多的玩家
        Long wolfKillTarget = this.actionTarget(Action.WOLF_CHOICE);
        if (wolfKillTarget != null) {
            // 告诉狼人，杀了谁了
            jsonObject = new JSONObject(){{ put("kill-target", wolfKillTarget); }};
            gameService.sendMessage(room.getWolfs(), Action.WOLF_KILL, jsonObject);
            room.addCacheAction(new PlayerAction(Action.WOLF_KILL, wolfKillTarget, wolfKillTarget));
        }
        // 狼人没杀人
        else {
            jsonObject = new JSONObject(){{ put("kill-target", null); }};
            gameService.sendMessage(room.getWolfs(), Action.WOLF_KILL, jsonObject);
        }
    }

    // 下发，女巫救人或毒杀
    private void callWitch() {
        Long wolfKillTarget = this.actionTarget(Action.WOLF_KILL);
        // 如果狼有杀人，让女巫救人
        if (wolfKillTarget!=null) {
            JSONObject jsonObject = new JSONObject(){{
                put("wait-time", 10);
                put("help-target", wolfKillTarget);
            }};
            gameService.sendMessage(room.getWitch(), Action.WITCH_CALL, jsonObject);
            this.waitPlayerAction(10, Action.WITCH_CHOICE);
        }
        // 如果女巫没有救人，女巫可以毒杀
        if (this.actionTarget(Action.WITCH_CHOICE)!=null) {
            gameService.sendMessage(room.getWitch(), Action.WITCH_CALL, null);
            this.waitPlayerAction(10, Action.WITCH_CHOICE);
        }
    }

    // 下发，猎人杀人
    private void callHunter() {
        Long witchKillTarget = this.actionTarget(Action.WITCH_CHOICE);
        Long wolfKillTarget  = this.actionTarget(Action.WOLF_KILL);
        // 如果猎人被狼杀，或被女巫杀
        if ((witchKillTarget!=null && witchKillTarget.equals(room.getHunter().getId())) || (wolfKillTarget!=null && wolfKillTarget.equals(room.getHunter().getId()))) {
            JSONObject jsonObject = new JSONObject(){{
                put("wait-time", 10);
            }};
            gameService.sendMessage(room.getWitch(), Action.HUNTER_CALL, jsonObject);
            this.waitPlayerAction(10, Action.HUNTER_CHOICE);
        }
    }

    // 下发，进入白天
    private void enterDay() {
        this.callHunter();
        // 统计晚上的杀人情况
        this.nightResult();
        // 如果胜利
        if (this.checkVictory()!=0) {
            gameService.sendMessage(room, Action.SHOW_RESULT, null);
            return;
        }
        // 玩家发言
        this.callAllPlayerSpeak();
        // 玩家投票
        this.callAllPlayerVote();
        // 统白天的杀人情况
        this.dayResult();
        // 如果胜利
        if (this.checkVictory()!=0) {
            gameService.sendMessage(room, Action.SHOW_RESULT, null);
            return;
        }
        // 清空 action
        room.resetCacheAction();
        // 进入夜晚
        this.enterNight();
    }

    // 汇总夜晚的结果
    private void nightResult() {
        Long hunterKillTarget = this.actionTarget(Action.HUNTER_CHOICE);
        Long witchKillTarget = this.actionTarget(Action.WITCH_CHOICE);
        Long wolfKillTarget  = this.actionTarget(Action.WOLF_KILL);

        // 如果，都是空
        if (wolfKillTarget==null && witchKillTarget==null) {
            // 平安夜
            gameService.sendMessage(room, Action.SHOW_SAFE_NIGHT, null);
        }
        else {
            gameService.sendMessage(room, Action.SHOW_PLAYER_DIE,
                new Long[]{wolfKillTarget, witchKillTarget, hunterKillTarget});
        }
    }

    // 汇总白天的结果
    private void dayResult() {
        // 检查票杀的目标
        Long voteKillTarget = this.actionTarget(Action.PLAYER_VOTE);
        // 谁被杀了
        gameService.sendMessage(room, Action.SHOW_PLAYER_DIE, new Long[]{voteKillTarget});
        // 如果票猎人，出发猎人动作
        if (room.getHunter().getId().equals(voteKillTarget)) {
            this.callHunter();
        }
    }

    // 所有存活玩家发言
    private void callAllPlayerSpeak() {
        for(Player player : room.getSeats()) {
            if (player!=null && player.isLiving()) {
                JSONObject jsonObject = new JSONObject(){{
                    put("wait-time", 30);
                }};
                gameService.sendMessage(player, Action.PLAYER_SPEAK, jsonObject);
                this.waitPlayerAction(10, Action.PLAYER_SPEAK);
            }
        }
    }

    // 所有存活玩家投票
    private void callAllPlayerVote() {
        JSONObject jsonObject = new JSONObject(){{
            put("wait-time", 30);
        }};
        gameService.sendMessage(room, Action.PLAYER_VOTE, jsonObject);
        this.waitPlayerAction(30, Action.PLAYER_VOTE);
    }

    // 检查胜利
    private int checkVictory() {
        int wolfCount = 0;
        int personCount = 0;
        for(Player player : room.getSeats()) {
            if (player!=null && player.isLiving()) {
                if (player.getIdentity()==Identity.WOLF) {
                    wolfCount++;
                }
                else {
                    personCount++;
                }
            }
        }
        // 游戏没结束
        if (wolfCount!=0 && personCount!=0) {
            return 0;
        }
        // 好人赢
        else if (personCount!=0) {
            return 1;
        }
        // 狼赢
        else {
            return 2;
        }
    }



    // 获取 Action 目标最多的人
    public Long actionTarget(String action) {
        Map<Long, PlayerAction> playerActions = room.getCacheActions(action);
        Map<Long, Integer> actionNumbers = new HashMap<>();
        for(PlayerAction playerAction : playerActions.values()) {
            Long actionKey = playerAction.getTargetPlayer();
            Integer actionNumber = actionNumbers.get(actionKey);
            actionNumber = (actionNumber==null) ? 1 : actionNumber + 1;
            actionNumbers.put(actionKey, actionNumber);
            if (actionNumber>playerActions.size()/2) {
                return playerAction.getTargetPlayer();
            }
        }
        return null;
    }

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
