package com.doopp.gauss.server.websocket;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.dao.RoomDao;
import com.doopp.gauss.common.dao.UserDao;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameSocketHandler extends AbstractWebSocketHandler {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(GameSocketHandler.class);

    // socket`s session
    private static final Map<Long, WebSocketSession> sockets = new HashMap<>();

    @Resource
    private RoomDao roomDao;

    @Resource
    private UserDao userDao;

    // text message
    @Override
    protected void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws Exception {

        // 校验消息
        JSONObject messageObject = JSONObject.parseObject(message.getPayload());
        // get action
        String action = messageObject.getString("action");

        // 如果 Action 异常
        if (action==null) {
            // socketSession.close();
        }
        // 聊天
        else if (action.equals("action-speak")) {
            this.actionSpeak(socketSession, messageObject);
        }
        // 游戏操作
        else if (action.equals("action-play")) {
            this.actionPlay(socketSession, messageObject);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession socketSession) throws Exception {
        User sessionUser = userDao.getUserBySocketSession(socketSession);
        Room sessionRoom = roomDao.userJoinRoom(sessionUser);
        socketSession.getAttributes().put("roomId", sessionRoom.getId());
        sockets.put(sessionUser.getId(), socketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession socketSession, CloseStatus status) throws Exception {
        User sessionUser = userDao.getUserBySocketSession(socketSession);
        Room sessionRoom = roomDao.getRoomBySocketSession(socketSession);
        roomDao.userLeaveRoom(sessionRoom.getId(), sessionUser);
        socketSession.getAttributes().remove("sessionUser");
        socketSession.getAttributes().remove("sessionRoomId");
        sockets.remove(sessionUser.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession socketSession, Throwable exception) throws Exception {
        if (socketSession.isOpen()) {
            socketSession.close();
        }
    }

    // 房间内公共频道说话
    private void actionSpeak(WebSocketSession socketSession, JSONObject messageObject) throws IOException {
        WebSocketSession[] inRoomSockets = this.getSocketsInRoom(socketSession);
        User sessionUser = userDao.getUserBySocketSession(socketSession);
        for (WebSocketSession userSocketSession : inRoomSockets) {
            if (!userSocketSession.equals(socketSession)) {
                messageObject.put("sender", sessionUser.getNickName());
                TextMessage message = new TextMessage(messageObject.toJSONString());
                userSocketSession.sendMessage(message);
            }
        }
    }

    // 房间内公共频道说话
    private void actionPlay(WebSocketSession socketSession, JSONObject messageObject) {
    }

    // 取出当前用户所在房间的 sockets
    private WebSocketSession[] getSocketsInRoom(WebSocketSession socketSession) {
        User[] users = userDao.getUsersInRoom(socketSession);
        WebSocketSession[] inRoomSockets = {};
        int ii = inRoomSockets.length;
        for (User user : users) {
            if (user!=null) {
                inRoomSockets[ii] = sockets.get(user.getId());
                ii++;
            }
        }
        return inRoomSockets;
    }
}
