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
    private static final Map<Integer, RoomEntity> roomSessions = new HashMap<>();

    // socket's session
    private static final Map<Long, WebSocketSession> socketSessions = new HashMap<>();

    // last created room id
    private static int lastRoomId = 54612;

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
    }

    @Override
    public void handleMessage(WebSocketSession socketSession, WebSocketMessage<?> message) throws Exception {
        // 要求必须进入房间
        if (!this.isJoinRoom(socketSession)) {
            sessionJoinRoom(socketSession, (TextMessage) message);
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
            case "chatRoom" :
                // 房间 ID
                int roomId = (int) socketSession.getAttributes().get("roomId");
                // 房间
                RoomEntity roomSession = roomSessions.get(roomId);
                // delegate
                chatRoomRule.handleTextMessage(currentUser, roomSession, messageObject);
                break;
            // 玩狼人杀
            case "wereWolf" :
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
        this.leaveRoom(currentUser, socketSession);
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
     * 处理还没加入房间时
     */
    private void sessionJoinRoom(WebSocketSession socketSession, TextMessage message) {
        UserEntity currentUser = (UserEntity) socketSession.getAttributes().get("currentUser");
        if (!this.isJoinRoom(socketSession)) {
            // 没有进入房间前，用户消息，被限定在 加入房间，或创建 房间
            JSONObject messageObject = this.message2Object(message);
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
        }
    }

    /*
     * 新建一个房间的对象
     */
    private RoomEntity newRoomSession(UserEntity user, JSONObject messageObject) {
        JSONObject actionData = messageObject.getObject("data", JSONObject.class);
        String roomName = actionData.getString("roomName");
        RoomEntity roomSession = new RoomEntity();
        synchronized ("createChatRoom") {
            roomSession.setId(++lastRoomId);
        }
        roomSession.setName(roomName);
        roomSession.setOwner(user);
        return roomSession;
    }

    /*
     * 创建新房间
     */
    private boolean createRoom(UserEntity user, JSONObject messageObject, WebSocketSession socketSession) {
        RoomEntity roomSession = this.newRoomSession(user, messageObject);
        int roomId = roomSession.getId();
        // 标注加入了房间
        socketSession.getAttributes().put("roomId", roomId);
        // 标注状态是加入房间聊天
        socketSession.getAttributes().put("socketRule", "chatRoom");
        // 创建 room
        roomSessions.put(roomId, roomSession);
        return true;
    }

    /*
     * 进入已经建立的房间
     */
    private boolean joinRoom(UserEntity user, JSONObject messageObject, WebSocketSession socketSession) {
        // 不过不能反解
        JSONObject actionData = messageObject.getObject("data", JSONObject.class);
        if (Objects.equal(actionData, null)) {
            return false;
        }
        // 如果没有找到 roomId
        int roomId = actionData.getInteger("roomId");
        if (roomId==0) {
            return false;
        }
        // 如果没有 roomSession 存在
        RoomEntity roomSession = roomSessions.get(roomId);
        if (Objects.equal(roomSession, null)) {
            return false;
        }
        // 标注加入了房间
        socketSession.getAttributes().put("roomId", roomId);
        // 标注状态是加入房间聊天
        socketSession.getAttributes().put("socketRule", "chatRoom");
        // 加入到房间
        roomSessions.get(roomId).joinWatch(user);
        return true;
    }

    /*
     * check is join room
     */
    private boolean isJoinRoom(WebSocketSession socketSession) {
        Object isJoinRoom = socketSession.getAttributes().get("roomId");
        return !Objects.equal(isJoinRoom, null);
    }

    /*
     * 离开房间
     */
    private void leaveRoom(UserEntity user, WebSocketSession socketSession) {
        if (isJoinRoom(socketSession)) {
            int roomId = (int) socketSession.getAttributes().remove("roomId");
            RoomEntity roomSession =  roomSessions.get(roomId);
            if (!Objects.equal(roomSession, null)) {
                roomSessions.get(roomId).userLeave(user);
                // 暂未加入删除房间的逻辑，离开后，房间为空，应该将房间删除
            }
        }
    }

    private JSONObject message2Object(TextMessage message) {
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
