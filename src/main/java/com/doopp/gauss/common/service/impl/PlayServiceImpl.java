package com.doopp.gauss.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.dao.RoomDao;
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
    private RoomDao roomDao;

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
    public void readyAction(Room room, Player player) {
        boolean allReady = true;
        for(int ii=0; ii<room.getPlayers().length; ii++) {
            if (room.getPlayers()[ii]==null) {
                allReady = false;
                continue;
            }
            if (room.getPlayers()[ii]==player) {
                room.getPlayers()[ii].setStatus(1);
            }
            if (room.getPlayers()[ii].getStatus()==0) {
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
    public void werewolfAction(Room room, Player player, JSONObject messageObject) {
        Long choiceTarget = messageObject.getLong("choice-target");
        Player choicePlayer = roomDao.getPlayerById(room, choiceTarget);
        roomDao.wolfChoiceKill(player, choicePlayer);
    }

    // 上行，预言家查身份
    @Override
    public void seerAction(Room room, Player player, JSONObject messageObject) {
        Long choiceTarget = messageObject.getLong("choice-target");
        Player choicePlayer = roomDao.getPlayerById(room, choiceTarget);
        roomDao.seerChoiceCheck(player, choicePlayer);
    }

    // 上行，女巫救人或毒杀
    @Override
    public void witchAction(Room room, Player player, JSONObject messageObject) {
        Long choiceTarget = messageObject.getLong("choice-target");
        Player choicePlayer = roomDao.getPlayerById(room, choiceTarget);
        roomDao.witchChoiceKill(player, choicePlayer);
        roomDao.witchChoiceHelp(player, choicePlayer);
    }

    // 上行，猎人杀人
    @Override
    public void hunterAction(Room room, Player player, JSONObject messageObject) {
        Long choiceTarget = messageObject.getLong("choice-target");
        Player choicePlayer = roomDao.getPlayerById(room, choiceTarget);
        room.hunterChoiceKill(player, choicePlayer);
    }

    // 发送信息
    @Override
    public void sendMessage(Player player, String message) {
        if (player!=null) {
            TextMessage textMessage = new TextMessage(message);
            WebSocketSession socketSession = gameSocketHandler.getSocket(player.getId());
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
