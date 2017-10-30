package com.doopp.gauss.server.websocket;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.server.websocket.realm.ChatRoomRealm;
import com.doopp.gauss.server.websocket.realm.WereWolfRealm;
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

    // 域标记
    public static final String WERE_WOLF_SOCKET_REALM = "WERE_WOLF_SOCKET_REALM";
    public static final String CHAT_ROOM_SOCKET_REALM = "CHAT_ROOM_SOCKET_REALM";

    // chat room realm
    @Autowired
    private ChatRoomRealm chatRoomRule;

    // were wolf realm
    @Autowired
    private WereWolfRealm wereWolfRule;

    @Override
    public void afterConnectionEstablished(WebSocketSession socketSession) throws Exception {
        // init socket realm
        socketSession.getAttributes().put("socketRealm", "");
        // get current User
        UserEntity currentUser = (UserEntity) socketSession.getAttributes().get("currentUser");
        Long sessionId = currentUser.getId();
        // close old socket session
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
        String socketRealm = (String) socketSession.getAttributes().get("socketRealm");
        // 当前用户
        UserEntity currentUser = (UserEntity) socketSession.getAttributes().get("currentUser");
        // 房间 ID
        int roomId = (int) socketSession.getAttributes().get("roomId");
        // 房间
        RoomEntity roomSession = chatRoomRule.getRoomSession(roomId);
        // 规则转发
        switch (socketRealm) {
            // 在聊天室
            case CHAT_ROOM_SOCKET_REALM :
                // delegate
                chatRoomRule.handleTextMessage(currentUser, roomSession, messageObject);
                break;
            // 玩狼人杀
            case WERE_WOLF_SOCKET_REALM :
                wereWolfRule.handleTextMessage(currentUser, roomSession, messageObject);
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

    /*
     * 关闭用户连接
     *
     * @param socketSession 要手动关闭的 socket 连接
     */
    public void closeConnection (UserEntity user) {
        Long userId = user.getId();
        if (userId!=null) {
            WebSocketSession socketSession = socketSessions.get(userId);
            if (socketSession!=null) {
                this.closeConnection(socketSession);
            }
        }
    }
}
