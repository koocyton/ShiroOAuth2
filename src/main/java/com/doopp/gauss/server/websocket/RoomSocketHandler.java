package com.doopp.gauss.server.websocket;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomSession;
import com.doopp.gauss.api.entity.UserEntity;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RoomSocketHandler extends AbstractWebSocketHandler {

    // socket`s session
    private static final Map<Long, WebSocketSession> sockets = new HashMap<>();

    // room`s session
    private static final Map<Integer, RoomSession> rooms = new HashMap<>();

    // room id
    private static int lastRoomId = 54612;

    // text message
    @Override
    protected void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws Exception {
        this.roomFilter(socketSession, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession socketSession, CloseStatus status) throws Exception {
        // get room id
        Object objRoomId = socketSession.getAttributes().get("roomId");
        if (objRoomId!=null) {
            int roomId = (int) objRoomId;
            UserEntity currentUser = (UserEntity) socketSession.getAttributes().get("currentUser");
            // remove from rooms , sockets, socketSession.roomId
            socketSession.getAttributes().remove("roomId");
            rooms.remove(roomId);
            sockets.remove(currentUser.getId());
        }
    }

    /*
     * 获取房间列表
     */
    public Map<Integer, RoomSession> getRooms() {
        return rooms;
    }

    private void roomFilter(WebSocketSession socketSession, TextMessage message) throws Exception {
        // 房间 ID
        Object roomId = socketSession.getAttributes().get("roomId");
        if (Objects.isNull(roomId)) {
            return;
        }
        // 校验消息
        JSONObject ObjMessage = JSONObject.parseObject(message.getPayload());
        // get action
        String messageAction = ObjMessage.getString("action");
        // get action data
        JSONObject messageData = ObjMessage.getJSONObject("data");
        // user
        UserEntity sendUser = (UserEntity) socketSession.getAttributes().get("currentUser");
        // 创建房间
        if (messageAction.equals("join-room")) {
            // 房间名
            String roomName = messageData.getString("roomName");
            this.createRoom(sendUser, roomName, socketSession);
            return;
        }
        // 加入到房间
        else if (messageAction.equals("create-room")) {
            int joinRoomId = messageData.getInteger("roomId");
            this.joinRoom(sendUser, joinRoomId, socketSession);
            return;
        }
        socketSession.close();
    }

    /*
     * 房主创建房间
     */
    private void createRoom(UserEntity owner, String roomName, WebSocketSession socketSession) {
        RoomSession roomSession = new RoomSession();
        synchronized ("createChatRoom") {
            roomSession.setId(++lastRoomId);
        }
        roomSession.setName(roomName);
        roomSession.setOwner(owner);
        // 保存在房间列表
        rooms.put(roomSession.getId(), roomSession);
        // cache 在 socket 列表
        sockets.put(owner.getId(), socketSession);
        // cache socket in room
        socketSession.getAttributes().put("roomId", roomSession.getId());
    }

    /*
     * 用户加入房间，普通状态
     */
    private void joinRoom(UserEntity watchUser, int roomId, WebSocketSession socketSession) {
        RoomSession roomSession = rooms.get(roomId);
        roomSession.joinWatch(watchUser);
        // cache 在 socket 列表
        sockets.put(watchUser.getId(), socketSession);
        // cache socket in room
        socketSession.getAttributes().put("roomId", roomSession.getId());
    }
}
