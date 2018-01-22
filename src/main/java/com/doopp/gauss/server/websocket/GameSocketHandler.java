package com.doopp.gauss.server.websocket;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.dao.RoomDao;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.service.PlayService;
import lombok.Getter;
import lombok.Setter;
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

    // room`s session
    private static final Map<Integer, Room> rooms = new HashMap<>();

    // freeRoom`s session
    private static final Map<Integer, Integer> freeRoomIds = new HashMap<>();

    // room id
    private static int lastRoomId = 54612;

    @Resource
    private RoomDao roomDao;

    @Resource
    private PlayService playService;

    // text message
    @Override
    protected void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws Exception {
        // 校验消息
        JSONObject messageObject = JSONObject.parseObject(message.getPayload());
        // get action
        String action = messageObject.getString("action");

        // 如果 Action 异常
        if (action==null) {
            logger.info("handleTextMessage : a null action message");
        }
        // 聊天
        else if (action.equals("speak")) {
            this.actionSpeak(socketSession, messageObject);
        }
        // 游戏操作
        else if (action.contains("play-")) {
            this.actionPlay(socketSession, messageObject, action);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession socketSession) throws Exception {
        socketSession.sendMessage(new TextMessage("{\"action\":\"player-connected\"}"));
        Player player = (Player) socketSession.getAttributes().get("sessionPlayer");
        roomDao.playerJoinRoom(player);
        sockets.put(player.getId(), socketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession socketSession, CloseStatus status) throws Exception {
        Player player = (Player) socketSession.getAttributes().get("sessionPlayer");
        roomDao.playerLeaveRoom(player.getRoomId(), player);
        socketSession.getAttributes().remove("sessionPlayer");
        sockets.remove(player.getId());
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
        Player player = (Player) socketSession.getAttributes().get("sessionPlayer");
        for (WebSocketSession userSocketSession : inRoomSockets) {
            if (!userSocketSession.equals(socketSession)) {
                messageObject.put("sender", player.getNickName());
                TextMessage message = new TextMessage(messageObject.toJSONString());
                userSocketSession.sendMessage(message);
            }
        }
    }

    // 网游戏
    private void actionPlay(WebSocketSession socketSession, JSONObject messageObject, String action) {
        Player player = (Player) socketSession.getAttributes().get("sessionPlayer");
        Room room = roomDao.getRoomById(player.getRoomId());
        playService.actionDispatcher(room, player, action, messageObject);
    }

    // 取出当前用户所在房间的 sockets
    private WebSocketSession[] getSocketsInRoom(WebSocketSession socketSession) {
        Player player = (Player) socketSession.getAttributes().get("sessionPlayer");
        Room room = roomDao.getRoomById(player.getRoomId());
        Player[] players = room.getPlayers();
        WebSocketSession[] inRoomSockets = {};
        int ii = inRoomSockets.length;
        for (Player otherPlayer : players) {
            if (otherPlayer!=null) {
                inRoomSockets[ii] = sockets.get(otherPlayer.getId());
                ii++;
            }
        }
        return inRoomSockets;
    }

    public WebSocketSession getWebsocketById(Long userId) {
        return sockets.get(userId);
    }

    public Room getRoomById(int roomId) {
        return rooms.get(roomId);
    }

    public Map<Integer, Integer> getFreeRoomIds() {
        return freeRoomIds;
    }
}
