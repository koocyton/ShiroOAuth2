package com.doopp.gauss.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.RoomSocketService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service("roomSocketService")
public class RoomSocketServiceImpl implements RoomSocketService {

    // log
    private final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    // room's session
    private static final Map<Integer, RoomEntity> roomSessions = new HashMap<>();

    // socket's session
    private static final Map<Long, WebSocketSession> socketSessions = new HashMap<>();

    // last created room id
    private static int lastRoomId = 54612;

    /*
     * 当建立连接时
     */
    @Override
    public void afterConnectionEstablished(UserEntity currentUser, WebSocketSession socketSession) throws IOException {
        Long sessionId = currentUser.getId();
        WebSocketSession oldSession = socketSessions.get(currentUser.getId());
        if (oldSession!=null && oldSession.isOpen()) {
            oldSession.close();
            socketSessions.remove(sessionId);
        }
        // add session map
        socketSessions.put(sessionId, socketSession);
    }

    /*
     * check is join room
     */
    private boolean isJoinRoom(WebSocketSession socketSession) {
        Object isJoinRoom = socketSession.getAttributes().get("roomId");
        return isJoinRoom==null;
    }

    /*
     * 新建一个房间的对象
     */
    private RoomEntity newRoomSession(UserEntity currentUser, JSONObject messageObject) {
        JSONObject actionData = messageObject.getObject("data", JSONObject.class);
        String roomName = actionData.getString("roomName");
        RoomEntity roomSession = new RoomEntity();
        synchronized ("createChatRoom") {
            roomSession.setId(++lastRoomId);
        }
        roomSession.setName(roomName);
        roomSession.setOwner(currentUser);
        return roomSession;
    }

    /*
     * 创建新房间
     */
    private boolean createRoom(UserEntity currentUser, JSONObject messageObject, WebSocketSession socketSession) {
        RoomEntity roomSession = this.newRoomSession(currentUser, messageObject);
        int roomId = roomSession.getId();
        socketSession.getAttributes().put("roomId", roomId);
        roomSessions.put(roomId, roomSession);
        return true;
    }

    /*
     * 进入已经建立的房间
     */
    private boolean joinRoom(UserEntity currentUser, JSONObject messageObject, WebSocketSession socketSession) {
        JSONObject actionData = messageObject.getObject("data", JSONObject.class);
        int roomId = actionData.getInteger("roomId");
        socketSession.getAttributes().put("roomId", roomId);
        roomSessions.get(roomId).joinWatch(currentUser);
        return true;
    }

    /*
     * 离开房间
     */
    private void leaveRoom(UserEntity currentUser, WebSocketSession socketSession) {
        if (isJoinRoom(socketSession)) {
            logger.info(" >>> " + socketSession);
            int roomId = (int) socketSession.getAttributes().remove("roomId");
            roomSessions.get(roomId).userLeave(currentUser);
        }
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
            // 根据 action 执行
            switch(action) {
                // 创建房间
                case "createRoom":
                    if (!this.createRoom(currentUser, messageObject, socketSession)) {
                        this.closeConnection(socketSession);
                    }
                    break;
                // 加入房间
                case "joinRoom":
                    if (!this.joinRoom(currentUser, messageObject, socketSession)) {
                        this.closeConnection(socketSession);
                    }
                    break;
                // 默认
                default:
                    this.closeConnection(socketSession);
                    break;
            }
            return;
        }
    }

    /*
     * 当连接关闭时
     */
    @Override
    public void afterConnectionClosed(UserEntity currentUser, WebSocketSession socketSession, CloseStatus status) {
        // remove session from map
        socketSessions.remove(currentUser.getId());
        // leave room
        this.leaveRoom(currentUser, socketSession);
    }

    /**
     * 关闭某个连接
     *
     * @param socketSession 要手动关闭的 socket 连接
     */
    private void closeConnection (WebSocketSession socketSession) {
        if (socketSession!=null && socketSession.isOpen()) {
            try {
                socketSession.close();
            }
            catch(Exception e) {
            }
        }
    }
}
