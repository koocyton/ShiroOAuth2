package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.RoomSocketService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Service("roomSocketService")
public class RoomSocketServiceImpl implements RoomSocketService {

    private static final Map<Integer, RoomSession> roomsSession = new HashMap<>();

    private static final Map<String, WebSocketSession> socketSessions = new HashMap<>();

    public void sendMessage(String message, RoomSession toSession) {

    }

    public void sendMessage(String message, WebSocketSession toSessions) {

    }

    public void onConnectEstablished(WebSocketSession webSocketSession) {
        // 获取当前用户
        UserEntity currentUser = (UserEntity) webSocketSession.getAttributes().get("currentUser");
        String sessionId = Long.toString(currentUser.getId());
    }

    public void onConnectClosed() {

    }

    public void onMessage() {

    }
}
