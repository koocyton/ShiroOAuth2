package com.doopp.gauss.api.game.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.game.RoomGame;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service("werewolfGame")
public class WerewolfGame implements RoomGame {

    @Override
    public void handleTextMessage(WebSocketSession socketSession, RoomEntity theRoom, UserEntity sendUser, JSONObject message) {

    }
}
