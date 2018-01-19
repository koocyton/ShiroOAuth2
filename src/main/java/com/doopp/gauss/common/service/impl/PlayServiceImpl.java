package com.doopp.gauss.common.service.impl;

import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.PlayService;
import com.doopp.gauss.server.websocket.GameSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service("playService")
public class PlayServiceImpl implements PlayService {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(PlayServiceImpl.class);

    @Autowired
    GameSocketHandler gameSocketHandler;

    @Override
    public void callAllSpeak(Room room) {
        for(User user : room.getUsers()) {
            if (user.isLive()) {
                this.callOneSpeak(room, user);
            }
        }
    }

    @Override
    public void callOneSpeak(User user) {
        sendMessage(user, "{\"callOneSpeak\":}");
        delay(30);
    }

    // 发送信息
    private void sendMessage(User user, String message) {
        TextMessage textMessage = new TextMessage(message);
        WebSocketSession socketSession = gameSocketHandler.getSocket(user.getId());
        try {
            socketSession.sendMessage(textMessage);
        }
        catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    // 延迟一段时间
    private static void delay(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
