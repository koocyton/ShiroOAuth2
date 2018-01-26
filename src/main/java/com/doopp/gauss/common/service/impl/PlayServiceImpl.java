package com.doopp.gauss.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.dao.PlayerDao;
import com.doopp.gauss.common.dao.RoomDao;
import com.doopp.gauss.common.defined.Action;
import com.doopp.gauss.common.defined.Identity;
import com.doopp.gauss.common.entity.PlayerAction;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.service.PlayService;
import com.doopp.gauss.common.task.WerewolfGameTask;
import com.doopp.gauss.server.websocket.GameSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;

@Service("playService")
public class PlayServiceImpl implements PlayService {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(PlayServiceImpl.class);

    @Resource
    private PlayerDao playerDao;

    @Resource
    private RoomDao roomDao;

    @Autowired
    private GameSocketHandler gameSocketHandler;

    @Autowired
    private ThreadPoolTaskExecutor gameTaskExecutor;

    // 用户发送的命令转发
    @Override
    public void actionDispatcher(Room room, Player player, String playerAction, JSONObject messageObject) {
        // 校验房间正在等待的状态
        if (room.getWaitAction().equals(playerAction)) {
            // 转发 action
            switch (playerAction) {
                // 用户准备
                case Action.PLAYER_READY:
                    this.readyAction(room, player);
                    break;
                // 狼选择杀人
                case Action.WOLF_CHOICE:
                    this.werewolfAction(room, player, messageObject);
                    break;
                // 先知选择查看
                case Action.SEER_CHOICE:
                    this.seerAction(room, player, messageObject);
                    break;
                // 女巫选择杀人或救人
                case Action.WITCH_CHOICE:
                    this.witchAction(room, player, messageObject);
                    break;
                // 猎人杀人
                case Action.HUNTER_CHOICE:
                    this.hunterAction(room, player, messageObject);
                    break;
                // 玩家说话
                case Action.PLAYER_SPEAK:
                    // this.playerSpeak(room, player, messageObject);
                    break;
            }
        }
    }

    // 接受用户准备好了的消息
    @Override
    public void readyAction(Room room, Player readyPlayer) {
        boolean allReady = true;
        for(Player player : playerDao.getPlayersByRoom(room)) {
            // 如果房间内找到用户，设定玩家准备好了
            if (player!=null && player.getId().equals(readyPlayer.getId())) {
                player.setStatus(1);
            }
            // 循环获取玩家是否准备好了，如果如果有空座位 || 有没准备好的，设定 false
            if (player==null || player.getStatus()==0) {
                allReady = false;
            }
        }
        // 都准备好了，就开始游戏
        if (room.getStatus()==0 && allReady) {
            room.setStatus(1);
            room.setGameTask(new WerewolfGameTask(room));
            this.gameTaskExecutor.execute(room.getGameTask());
        }
    }

    // 上行，狼人杀人
    @Override
    public void werewolfAction(Room room, Player actionPlayer, JSONObject messageObject) {
        if (actionPlayer.getIdentity()==Identity.WOLF) {
            Long playerId = messageObject.getLong("choice-target");
            Player targetPlayer = playerDao.getPlayerById(playerId);
            room.setCacheAction(new PlayerAction(Action.WOLF_CHOICE, actionPlayer, targetPlayer));
            // 如果三个狼人都提交了，就 notify game task continue
            if (room.cacheActionCount(Action.WOLF_CHOICE)==3) {
                room.getGameTask().notify();
            }
        }
    }

    // 上行，预言家查身份
    @Override
    public void seerAction(Room room, Player actionPlayer, JSONObject messageObject) {
        if (actionPlayer.getIdentity()==Identity.SEER) {
            Long playerId = messageObject.getLong("choice-target");
            Player targetPlayer = playerDao.getPlayerById(playerId);
            roomDao.cacheAction(Action.SEER_CHOICE, actionPlayer, targetPlayer);
            this.sendMessage(actionPlayer, targetPlayer.getIdentity().toString());
        }
    }

    // 上行，女巫救人或毒杀
    @Override
    public void witchAction(Room room, Player actionPlayer, JSONObject messageObject) {
        if (actionPlayer.getIdentity()==Identity.WITCH) {
            Long playerId = messageObject.getLong("choice-target");
            Player targetPlayer = playerDao.getPlayerById(playerId);
            roomDao.cacheAction(Action.WITCH_CHOICE, actionPlayer, targetPlayer);
            // 如果女巫提交完毕
            if (room.cacheActionCount(Action.WITCH_CHOICE)==1) {
                room.getGameTask().notify();
            }
        }
    }

    // 上行，猎人杀人
    @Override
    public void hunterAction(Room room, Player actionPlayer, JSONObject messageObject) {
        if (actionPlayer.getIdentity()==Identity.HUNTER) {
            Long playerId = messageObject.getLong("choice-target");
            Player targetPlayer = playerDao.getPlayerById(playerId);
            roomDao.cacheAction(Action.HUNTER_CHOICE, actionPlayer, targetPlayer);
            // 如果猎人提交完毕
            if (room.cacheActionCount(Action.HUNTER_CHOICE)==1) {
                room.getGameTask().notify();
            }
        }
    }

    // 发送信息
    @Override
    public void sendMessage(Player[] players, String message) {
        for(Player player : players) {
            this.sendMessage(player, message);
        }
    }

    // 发送信息
    @Override
    public void sendMessage(Player[] players, String action, Object data) {
        for(Player player : players) {
            this.sendMessage(player, action, data);
        }
    }

    // 发送信息
    @Override
    public void sendMessage(Player player, String message) {
        if (player!=null) {
            TextMessage textMessage = new TextMessage(message);
            WebSocketSession socketSession = gameSocketHandler.getWebsocketById(player.getId());
            try {
                socketSession.sendMessage(textMessage);
            }
            catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    // 发送信息
    @Override
    public void sendMessage(Player player, String action, Object data) {
        if (player!=null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", action);
            jsonObject.put("data", data);
            this.sendMessage(player, jsonObject.toJSONString());
        }
    }

    // 发送信息
    @Override
    public void sendMessage(Room room, String message) {
        for(int ii=0; ii<room.getPlayers().length; ii++) {
            if (room.getPlayers()[ii]==null) {
                continue;
            }
            this.sendMessage(room.getPlayers()[ii], message);
        }
    }

    // 发送信息
    @Override
    public void sendMessage(Room room, String action, Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("data", data);
        this.sendMessage(room, jsonObject.toJSONString());
    }

    // 延迟一段时间
    @Override
    public void delay(float second) {
        try {
            Thread.sleep((int) second * 1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
