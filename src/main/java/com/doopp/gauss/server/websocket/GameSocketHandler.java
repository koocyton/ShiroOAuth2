package com.doopp.gauss.server.websocket;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.server.task.GameTaskDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameSocketHandler extends AbstractWebSocketHandler {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(GameSocketHandler.class);

    // socket`s session
    private static final Map<Long, WebSocketSession> sockets = new HashMap<>();

    // room`s session
    private static final Map<Integer, Room> rooms = new HashMap<>();

    // freeRoom`s session
    private static final Map<Integer, Room> freeRooms = new HashMap<>();

    // room id
    private static int lastRoomId = 54612;

    // text message
    @Override
    protected void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws Exception {

        // 校验消息
        JSONObject messageObject = JSONObject.parseObject(message.getPayload());
        // get action
        String action = messageObject.getString("action");

        // 如果 Action 异常
        if (action==null) {
            logger.info(" >>> no action in message ");
            // socketSession.close();
        }
        // 聊天
        else if (action.equals("action-speak")) {
            this.actionSpeak(user, room, sockets, action);
        }
        // 游戏操作
        else if (action.equals("action-play")) {
            this.actionPlay(user, room, sockets, action);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession socketSession, CloseStatus status) throws Exception {
        User sessionUser = (User) socketSession.getAttributes().get("sessionUser");
        sockets.remove(sessionUser.getId());
    }

    // 房间内公共频道说话
    private void actionSpeak(Room sessionRoom, JSONObject messageObject) throws IOException {
        // to json
        TextMessage message = new TextMessage(messageObject.toJSONString());
        // send to watch user
        for(User watchUser : sessionRoom.getWatchUsers().values()) {
            sockets.get(watchUser.getId()).sendMessage(message);
        }
        // send to owner user
        User owner = sessionRoom.getOwner();
        if (owner!=null) {
            sockets.get(owner.getId()).sendMessage(message);
        }
    }

    // 离开房间
    private void leaveRoom(User user) throws IOException {
        User sessionUser = (User) socketSession.getAttributes().get("sessionUser");
        Room sessionRoom = (Room) socketSession.getAttributes().get("sessionRoom");
        rooms.remove(sessionRoom.getId());
        // 如果是空房间
        if (sessionRoom.isSpaceRoom()) {
            freeRooms.remove(sessionRoom.getId());
        }
        // 不是空房间，就加入到空闲房间内
        else {
            freeRooms.put(sessionRoom.getId(), sessionRoom);
        }
        if (socketSession.isOpen()) {
            socketSession.close();
        }
    }

    private synchronized int joinRoom(User user) {
        // 找已经存在，有座位的房间，并加入
        for(Room room : freeRooms.values()) {
            // 房间内进入用户
            roomDao.userJoin(roomId, user);
            if (room.userJoin(user)) {
                // 如果座满，就从空闲房间列表移除，加入到满员房间列表
                if (room.allSeatsTaken()) {
                    freeRooms.remove(room.getId());
                    rooms.put(room.getId(), room);
                }
                return room.getId();
            }
        }
        // 创建一个空房间，并加入
        Room room = new Room();
        room.setId(++lastRoomId);
        rooms.put(room.getId(), room);
        return room.getId();
    }
}
