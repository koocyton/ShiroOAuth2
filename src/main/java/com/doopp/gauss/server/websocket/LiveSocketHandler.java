package com.doopp.gauss.server.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.server.websocket.rule.ChatRoomRule;
import com.doopp.gauss.server.websocket.rule.WereWolfRule;
import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * LiveSocketHandler
 *
 * Created by henry on 2017/10/27.
 */
public class LiveSocketHandler implements org.springframework.web.socket.WebSocketHandler {

    // log
    private final Logger logger = LoggerFactory.getLogger(LiveSocketHandler.class);

    // room's session
    // private static final Map<Integer, RoomEntity> roomSessions = new HashMap<>();

    // socket's session
    private static final Map<Long, WebSocketSession> socketSessions = new HashMap<>();

    // last created room id
    // private static int lastRoomId = 54612;

    public static final String WERE_WOLF_SOCKET_RULE = "WERE_WOLF_SOCKET_RULE";
    public static final String CHAT_ROOM_SOCKET_RULE = "CHAT_ROOM_SOCKET_RULE";

    // chat room rule
    @Autowired
    private ChatRoomRule chatRoomRule;

    // were wolf rule
    @Autowired
    private WereWolfRule wereWolfRule;

    @Override
    public void afterConnectionEstablished(WebSocketSession socketSession) throws Exception {
        UserEntity currentUser = (UserEntity) socketSession.getAttributes().get("currentUser");
        Long sessionId = currentUser.getId();
        WebSocketSession oldSession = socketSessions.get(currentUser.getId());
        if (oldSession!=null && oldSession.isOpen()) {
            this.closeConnection(oldSession);
            socketSessions.remove(sessionId);
        }
        // add session map
        socketSessions.put(sessionId, socketSession);
        //
        TextMessage textMessage = new TextMessage("{status:0}");
        socketSession.sendMessage(textMessage);
    }

    @Override
    public void handleMessage(WebSocketSession socketSession, WebSocketMessage<?> message) throws Exception {
        // 要求必须进入房间
        if (!chatRoomRule.isJoinRoom(socketSession)) {
            chatRoomRule.sessionJoinRoom(socketSession, (TextMessage) message);
        }
        else if (message instanceof TextMessage) {
            handleTextMessage(socketSession, (TextMessage) message);
        }
        else if (message instanceof BinaryMessage) {
            handleBinaryMessage(socketSession, (BinaryMessage) message);
        }
        else if (message instanceof PongMessage) {
            handlePongMessage(socketSession, (PongMessage) message);
        }
        else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
    }

    protected void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws Exception {
        // 检查到达的字符串
        JSONObject messageObject = JSONObject.parseObject(message.getPayload());
        if (Objects.equal(messageObject, null)) {
            return;
        }
        // 检查当前连接的规则
        String socketRule = (String) socketSession.getAttributes().get("socketRule");
        // 当前用户
        UserEntity currentUser = (UserEntity) socketSession.getAttributes().get("currentUser");
        // 规则转发
        switch (socketRule) {
            // 在聊天室
            case CHAT_ROOM_SOCKET_RULE :
                // 房间 ID
                int roomId = (int) socketSession.getAttributes().get("roomId");
                // 房间
                RoomEntity roomSession = chatRoomRule.getRoomSession(roomId);
                // delegate
                chatRoomRule.handleTextMessage(currentUser, roomSession, messageObject);
                break;
            // 玩狼人杀
            case WERE_WOLF_SOCKET_RULE :
                wereWolfRule.handleTextMessage(currentUser, socketSession, messageObject);
                break;
        }
    }

    protected void handleBinaryMessage(WebSocketSession socketSession, BinaryMessage message) throws Exception {
    }

    protected void handlePongMessage(WebSocketSession socketSession, PongMessage message) throws Exception {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession socketSession, CloseStatus status) throws Exception {
        UserEntity currentUser = (UserEntity) socketSession.getAttributes().get("currentUser");
        // remove session from map
        socketSessions.remove(currentUser.getId());
        // leave room
        chatRoomRule.leaveRoom(currentUser, socketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void pushMessage(UserEntity toUser, String message) {
        Long userId = toUser.getId();
        WebSocketSession socketSession = socketSessions.get(userId);
        try {
            if (socketSession!=null && socketSession.isOpen()) {
                TextMessage textMessage = new TextMessage(message);
                socketSession.sendMessage(textMessage);
            }
        }
        catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /*
     * 关闭某个连接
     *
     * @param socketSession 要手动关闭的 socket 连接
     */
    public void closeConnection (WebSocketSession socketSession) {
        if (socketSession!=null && socketSession.isOpen()) {
            try {
                socketSession.close();
            }
            catch(Exception e) {
            }
        }
    }
}
