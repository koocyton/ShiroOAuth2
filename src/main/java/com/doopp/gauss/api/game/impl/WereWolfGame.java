package com.doopp.gauss.api.game.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.game.RoomGame;
import com.doopp.gauss.api.message.RoomMessage;
import com.doopp.gauss.server.task.DaemonMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Service
public class WereWolfGame implements RoomGame {

    private final static Logger logger = LoggerFactory.getLogger(WereWolfGame.class);

    private final static int gameType = RoomEntity.WERE_WOLF_GAME;

    private String gameProgress = "gameJoin";

    private Map<Long, UserEntity> gameUser = new HashMap<>();


    @Override
    public void handleDaemonMessage(DaemonMessage daemonMessage) {
        logger.info(" >>> daemonMessage " + daemonMessage);
    }

    @Override
    public void handleTextMessage(WebSocketSession socketSession, RoomEntity sessionRoom, UserEntity sessionUser, JSONObject messageObject) {
        String action = messageObject.getString("action");
        if (action.equals("gameReady") && gameProgress.equals("gameJoin")) {
            gameUser.put(sessionUser.getId(), sessionUser);
        }
        // 发送消息到 task
        sessionRoom.getGameTask().roomMessageHandle(new RoomMessage("hello"));
    }

    @Override
    public void joinGame() {

    }

    @Override
    public void leaveGame() {

    }

    @Override
    public int getGameType() {
        return gameType;
    }
}
