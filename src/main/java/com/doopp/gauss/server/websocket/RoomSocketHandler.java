package com.doopp.gauss.server.websocket;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.RoomSession;
import com.doopp.gauss.api.entity.UserEntity;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

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

        // get room id
        Object roomId = socketSession.getAttributes().get("roomId");
        // get json object message
        JSONObject ObjMessage = JSONObject.parseObject(message.getPayload());
        // null message
        if (ObjMessage==null || ObjMessage.getString("action")==null) {
            return;
        }
        // get action
        String messageAction = ObjMessage.getString("action");
        // get action data
        JSONObject messageData = ObjMessage.getJSONObject("data");

        // 当前用户
        UserEntity sessionUser = (UserEntity) socketSession.getAttributes().get("currentUser");


        // 如果不在房间，必须先发送加入房间，或创建房间的指令
        if (roomId==null) {
            // 创建房间
            if (messageAction.equals("join-room")) {
                // 房间名
                String roomName = messageData.getString("roomName");
                this.createRoom(sessionUser, roomName, socketSession);
                return;
            }
            // 加入到房间
            else if (messageAction.equals("create-room")) {
                int joinRoomId = messageData.getInteger("roomId");
                this.joinRoom(sessionUser, joinRoomId, socketSession);
                return;
            }
            socketSession.close();
        }
        // 如果在房间，未参加活动
        else if (false) {

        }
        // 如果在房间内，并参加活动 - 狼人杀
        else if (false) {

        }
    }

    private void createRoom(UserEntity owner, String roomName, WebSocketSession socketSession) {
        RoomSession roomSession = new RoomSession();
        synchronized ("createChatRoom") {
            roomSession.setId(++lastRoomId);
        }
        roomSession.setName(roomName);
        roomSession.setOwner(owner);
        rooms.put(roomSession.getId(), roomSession);
        sockets.put(owner.getId(), socketSession);
    }

    private void joinRoom(UserEntity user, int roomId, WebSocketSession socketSession) {
        RoomSession roomSession = rooms.get(roomId);
        roomSession.joinWatch(user);
        sockets.put(user.getId(), socketSession);
    }
}
