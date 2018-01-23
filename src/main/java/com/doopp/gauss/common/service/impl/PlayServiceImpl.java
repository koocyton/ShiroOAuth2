package com.doopp.gauss.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.dao.PlayerDao;
import com.doopp.gauss.common.defined.Identity;
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

    @Autowired
    private GameSocketHandler gameSocketHandler;

    @Autowired
    private ThreadPoolTaskExecutor gameTaskExecutor;

    // 用户发送的命令转发
    @Override
    public void actionDispatcher(Room room, Player player, String playAction, JSONObject messageObject) {
        switch(playAction) {
            case "play-ready":
                this.readyAction(room, player);
                break;
            case "play-werewolf":
                this.werewolfAction(room, player, messageObject);
                break;
            case "play-seer":
                this.seerAction(room, player, messageObject);
                break;
            case "play-witch":
                this.witchAction(room, player, messageObject);
                break;
            case "play-hunter":
                this.hunterAction(room, player, messageObject);
                break;
        }
    }

    // 接受用户准备好了的消息
    @Override
    public void readyAction(Room room, Player readyPlayer) {
        boolean allReady = true;
        for(Player player : playerDao.getPlayersByRoom(room)) {
            // 如果房间内找到用户，设定玩家准备好了
            if (player.getId().equals(readyPlayer.getId())) {
                player.setStatus(1);
            }
            // 循环获取玩家是否准备好了，如果如果有空座位 || 有没准备好的，设定 false
            if (player==null || player.getStatus()==0) {
                allReady = false;
            }
        }
        // 都准备好了，就开始游戏
        if (allReady) {
            room.setStatus(2);
            this.gameTaskExecutor.execute(new WerewolfGameTask(room));
        }
    }

    // 上行，狼人杀人
    @Override
    public void werewolfAction(Room room, Player actionPlayer, JSONObject messageObject) {
        Long playerId = messageObject.getLong("choice-target");
        Player choicePlayer = playerDao.getPlayerById(playerId);
        for(Player player : playerDao.getPlayersByRoom(room)) {
            if (player.getIdentity()==Identity.WOLF) {
                if (Identity.choice) {

                }
            }
        }
    }

    // 上行，预言家查身份
    @Override
    public void seerAction(Room room, Player player, JSONObject messageObject) {
        Long playerId = messageObject.getLong("choice-target");
        Player choicePlayer = playerDao.getPlayerById(playerId);
        roomDao.seerChoiceCheck(player, choicePlayer);
    }

    // 上行，女巫救人或毒杀
    @Override
    public void witchAction(Room room, Player player, JSONObject messageObject) {
        Long playerId = messageObject.getLong("choice-target");
        Player choicePlayer = playerDao.getPlayerById(playerId);
        roomDao.witchChoiceKill(player, choicePlayer);
        roomDao.witchChoiceHelp(player, choicePlayer);
    }

    // 上行，猎人杀人
    @Override
    public void hunterAction(Room room, Player player, JSONObject messageObject) {
        Long playerId = messageObject.getLong("choice-target");
        Player choicePlayer = playerDao.getPlayerById(playerId);
        room.hunterChoiceKill(player, choicePlayer);
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
    public void sendMessage(Room room, String message) {
        for(int ii=0; ii<room.getPlayers().length; ii++) {
            this.sendMessage(room.getPlayers()[ii], message);
        }
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
