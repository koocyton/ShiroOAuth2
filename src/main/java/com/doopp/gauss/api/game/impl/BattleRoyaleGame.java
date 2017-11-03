package com.doopp.gauss.api.game.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.game.RoomGame;
import com.doopp.gauss.server.task.DaemonMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class BattleRoyaleGame implements RoomGame {

    private final static int gameType = RoomEntity.BATTLE_ROYALE_GAME;



    @Override
    public void handleDaemonMessage(DaemonMessage daemonMessage) {

    }

    @Override
    public void handleTextMessage(WebSocketSession socketSession, RoomEntity sessionRoom, UserEntity sessionUser, JSONObject messageObject) {

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
