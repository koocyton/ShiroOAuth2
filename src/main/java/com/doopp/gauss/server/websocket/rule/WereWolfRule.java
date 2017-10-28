package com.doopp.gauss.server.websocket.rule;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.UserEntity;
import org.springframework.web.socket.WebSocketSession;

public class WereWolfRule {

    public void handleTextMessage(UserEntity currentUser, WebSocketSession socketSession, JSONObject message) {

    }
}
