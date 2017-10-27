package com.doopp.gauss.api.service;

import com.doopp.gauss.api.entity.UserEntity;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public interface RoomSocketService {

    void afterConnectionEstablished(UserEntity currentUser, WebSocketSession socketSession);

    void handleTextMessage(UserEntity currentUser, WebSocketSession socketSession, TextMessage message);

    void afterConnectionClosed(UserEntity currentUser, WebSocketSession socketSession, CloseStatus status);
}
