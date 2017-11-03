package com.doopp.gauss.api.game.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.game.RoomGame;
import org.springframework.context.annotation.Scope;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Scope("prototype")
public class WereWolfGame implements RoomGame {

    private final static int gameType = RoomEntity.WERE_WOLF_GAME;

    private String gameProgress = "gameJoin";

    private Map<Long, UserEntity> gameUser = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession socketSession, RoomEntity sessionRoom, UserEntity sessionUser, JSONObject messageObject) {
        String action = messageObject.getString("action");
        if (action.equals("gameReady") && gameProgress.equals("gameJoin")) {
            gameUser.put(sessionUser.getId(), sessionUser);
        }
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
