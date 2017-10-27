package com.doopp.gauss.server.websocket.handler;

import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.RoomSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * GameSocketHandler
 *
 * Created by henry on 2017/10/27.
 */
// @Component
public class RoomSocketHandler extends AbstractWebSocketHandler {

    @Autowired
    RoomSocketService roomSocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 获取当前用户
        UserEntity user = (UserEntity) session.getAttributes().get("currentUser");
        roomSocketService.afterConnectionEstablished(user, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 获取当前用户
        UserEntity user = (UserEntity) session.getAttributes().get("currentUser");
        roomSocketService.handleTextMessage(user, session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 获取当前用户
        UserEntity user = (UserEntity) session.getAttributes().get("currentUser");
        roomSocketService.afterConnectionClosed(user, session, status);
    }
}
