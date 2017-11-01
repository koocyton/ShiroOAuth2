package com.doopp.gauss.api.game.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.game.RoomGame;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service("guessDrawGame")
public class GuessDrawGame implements RoomGame {

    private final static int gameType = RoomEntity.GUESS_DRAW_GAME;

    @Override
    public void handleTextMessage(WebSocketSession socketSession, RoomEntity theRoom, UserEntity sendUser, JSONObject message) {

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
