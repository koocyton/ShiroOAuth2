package com.doopp.gauss.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.RoomSocketService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service("roomSocketService")
public class RoomSocketServiceImpl implements RoomSocketService {

    private final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    private static final Map<Integer, RoomEntity> roomSessions = new HashMap<>();

    private static final Map<Long, WebSocketSession> socketSessions = new HashMap<>();

    /*
     * 当建立连接时
     */
    @Override
    public void afterConnectionEstablished(UserEntity currentUser, WebSocketSession socketSession) {
        // add session map
        socketSessions.put(currentUser.getId(), socketSession);
    }

    /*
     * is join room
     */
    private boolean isJoinRoom(WebSocketSession socketSession) {
        Object isJoinRoom = socketSession.getAttributes().get("isJoinRoom");
        return isJoinRoom==null;
    }

    /*
     * join room filter
     */
    private boolean joinRoom(UserEntity currentUser, JSONObject messageObject, WebSocketSession socketSession) {

        socketSession.getAttributes().put("isJoinRoom", "isJoinRoom");
        socketSession.getAttributes().put("roomId", "isJoinRoom");
        return true;
    }

    private JSONObject getJsonObject(TextMessage message) {
        // 获取返回的信息
        String requestBody = message.getPayload();
        // 如果获取到信息
        if (requestBody!=null && requestBody.length()>=1) {
            return JSON.parseObject(requestBody);
        }
        // error
        return new JSONObject();
    }

    /*
     * 当接收到文本消息时
     */
    @Override
    public void handleTextMessage(UserEntity currentUser, WebSocketSession socketSession, TextMessage message) {
        if (!this.isJoinRoom(socketSession)) {
            // 没有进入房间前，用户消息，被限定在 加入房间，或创建 房间
            JSONObject messageObject = this.getJsonObject(message);
            // 获取 action ( createRoom | joinRoom )
            String action = messageObject.getString("action");
            // join room
            if (action.equals("joinRoom")) {
                if(this.joinRoom(currentUser, messageObject, socketSession))
                {

                }
            }
            else {
                this.closeConnection(socketSession);
            }
            return;
        }
        logger.info(" >>> " + message);
    }

    /*
     * 当连接关闭时
     */
    @Override
    public void afterConnectionClosed(UserEntity currentUser, WebSocketSession socketSession, CloseStatus status) {
        // remove session from map
        socketSessions.remove(currentUser.getId());
    }

    /**
     * 关闭某个连接
     *
     * @param socketSession
     */
    private void closeConnection (WebSocketSession socketSession) {
        if (socketSession!=null && socketSession.isOpen()) {
            try {
                socketSession.close();
            }
            catch(Exception e)
            {
            }
        }
    }
}
