package com.doopp.gauss.server.websocket;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.game.RoomGame;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RoomSocketHandler extends AbstractWebSocketHandler {

    // socket`s session
    private static final Map<Long, WebSocketSession> sockets = new HashMap<>();

    // room`s session
    private static final Map<Integer, RoomEntity> rooms = new HashMap<>();

    // room id
    private static int lastRoomId = 54612;

    /*
     * 获取房间列表
     */
    public Map<Integer, RoomEntity> getRooms() {
        return rooms;
    }

    /*
     * 房间里说话
     */
    private void publicTalk(UserEntity sendUser, RoomEntity sessionRoom, TextMessage message) throws IOException {

        Map<Long, UserEntity> frontUsers = sessionRoom.getFrontUsers();
        for(UserEntity frontUser : frontUsers.values()) {
            sockets.get(frontUser.getId()).sendMessage(message);
        }
        Map<Long, UserEntity> watchUsers = sessionRoom.getWatchUsers();
        for(UserEntity watchUser : watchUsers.values()) {
            sockets.get(watchUser.getId()).sendMessage(message);
        }
        UserEntity owner = sessionRoom.getOwner();
        sockets.get(owner.getId()).sendMessage(message);
    }

    // text message
    @Override
    protected void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws Exception {

        // 在哪个房间
        RoomEntity sessionRoom = this.getSessionRoom(socketSession);
        // 是哪个用户
        UserEntity sendUser = this.getSessionUser(socketSession);
        // 参加什么活动
        RoomGame roomGame = this.getSessionGame(socketSession);

        // 如果没有进入房间
        if (sessionRoom==null) {
            this.openRoom(socketSession, message);
        }
        // 在房间内，且参加了游戏
        else if (roomGame!=null) {
            roomGame.handleTextMessage(socketSession, sessionRoom, sendUser, message);
        }
        // 在房间内没有参加活动
        else {
            this.sendRoomMessage(sendUser, sessionRoom, message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession socketSession, CloseStatus status) throws Exception {
        // 关闭连接时，离开房间
        this.leaveRoom(socketSession);
    }

    // 房间内发送消息
    private void sendRoomMessage(UserEntity sendUser, RoomEntity sessionRoom, TextMessage message) throws Exception {

        JSONObject objMessage = JSONObject.parseObject(message.getPayload());
        objMessage.put("sendUserId", sendUser.getId());
        objMessage.put("sendUserName", sendUser.getNickname());

        String action = objMessage.getString("action");
        switch(action) {
            // 召唤游戏参与者
            case "callPlay" :
                this.callPlayer(sendUser, sessionRoom, objMessage);
                break;
            // 接受召唤参与游戏
            case "joinGame" :
                this.joinGame(sendUser, sessionRoom, objMessage);
                break;
            // 不参加游戏
            case "leaveGame" :
                this.leaveGame(sendUser, sessionRoom, objMessage);
                break;
            // 公频内说话
            default :
                TextMessage sendMessage = new TextMessage(JSONObject.toJSONString(objMessage));
                this.publicTalk(sendUser, sessionRoom, sendMessage);
        }
    }

    /*
     * 房主征集游戏玩家
     */
    private void callPlayer(UserEntity sendUser, RoomEntity sessionRoom, JSONObject objMessage) throws IOException {
        // 没有游戏在进行中的房间，才能发召唤
        RoomEntity.GameStatus gameStatus = sessionRoom.getGameStatus();
        if (!gameStatus.equals(RoomEntity.GameStatus.Resting)) {
            return;
        }
        // 房主才能征集游戏玩家
        if (sendUser.getId().equals(sessionRoom.getOwner().getId())) {
            sessionRoom.setGameStatus(RoomEntity.GameStatus.Calling);
            TextMessage sendMessage = new TextMessage(JSONObject.toJSONString(objMessage));
            this.publicTalk(sendUser, sessionRoom, sendMessage);
        }
    }

    /*
     * 接受征集，成为游戏玩家
     */
    private void joinGame(UserEntity sendUser, RoomEntity sessionRoom, JSONObject objMessage) {
        // 正在召唤的房间，才能加入
        RoomEntity.GameStatus gameStatus = sessionRoom.getGameStatus();
        if (!gameStatus.equals(RoomEntity.GameStatus.Calling)) {
            return;
        }
        // 加入游戏
        sessionRoom.joinGame(sendUser);
        // 如果加入到上限，就开始游戏
        if(sessionRoom.playerNumber()>=12) {
            this.playGame(sessionRoom);
        }
    }

    /*
     * 离开游戏
     */
    private void leaveGame(UserEntity sendUser, RoomEntity sessionRoom, JSONObject objMessage) {
        // 正在召唤的房间，才能操作
        RoomEntity.GameStatus gameStatus = sessionRoom.getGameStatus();
        if (!gameStatus.equals(RoomEntity.GameStatus.Calling)) {
            return;
        }
        // 离开游戏
        sessionRoom.leaveGame(sendUser);
    }

    /*
     * 玩家征集完毕，开始游戏
     */
    private void playGame(RoomEntity sessionRoom) {
        for(UserEntity gameUser : sessionRoom.getGameUsers().values()) {
            TextMessage textMessage = new TextMessage("{action:\"playGame\", gameType:\"Werewolf\"}");
            try {
                sockets.get(gameUser.getId()).sendMessage(textMessage);
            }
            catch(IOException e) {
                continue;
            }
        }
        sessionRoom.setGameStatus(RoomEntity.GameStatus.Playing);
    }

    /*
     * 游戏结束
     */
    private void gameOver(RoomEntity sessionRoom) {
        sessionRoom.setGameStatus(RoomEntity.GameStatus.Resting);
    }

    /*
     * 获取房间
     */
    private RoomEntity getSessionRoom(WebSocketSession socketSession) {
        Object roomId = socketSession.getAttributes().get("roomId");
        if (socketSession.getAttributes().get("roomId")==null) {
            return null;
        }
        return rooms.get(roomId);
    }

    /*
     * 获取用户
     */
    private UserEntity getSessionUser(WebSocketSession socketSession) {
        return (UserEntity) socketSession.getAttributes().get("currentUser");
    }

    /*
     * 获取活动
     */
    private RoomGame getSessionGame(WebSocketSession socketSession) {
        return (RoomGame) socketSession.getAttributes().get("roomGame");
    }

    /*
     * 开房
     */
    private void openRoom(WebSocketSession socketSession, TextMessage message) throws Exception {
        // 校验消息
        JSONObject ObjMessage = JSONObject.parseObject(message.getPayload());
        // get action
        String messageAction = ObjMessage.getString("action");
        // get action data
        JSONObject messageData = ObjMessage.getJSONObject("data");
        // user
        UserEntity sendUser = (UserEntity) socketSession.getAttributes().get("currentUser");
        // 创建房间
        if (messageAction.equals("createRoom")) {
            // 房间名
            String roomName = messageData.getString("roomName");
            this.createRoom(sendUser, roomName, socketSession);
            return;
        }
        // 加入到房间
        else if (messageAction.equals("joinRoom")) {
            int joinRoomId = messageData.getInteger("roomId");
            this.joinRoom(sendUser, joinRoomId, socketSession);
            return;
        }
        socketSession.close();
    }

    /*
     * 离开房间
     */
    private void leaveRoom(WebSocketSession socketSession) {
        RoomEntity sessionRoom = this.getSessionRoom(socketSession);
        UserEntity sessionUser = this.getSessionUser(socketSession);
        if (sessionRoom!=null) {
            sessionRoom.userLeave(sessionUser);
            if (sessionRoom.getWatchUsers().size()==0 && sessionRoom.getFrontUsers().size()==0 && sessionRoom.getOwner()==null) {
                rooms.remove(sessionRoom.getId());
            }
        }
        sockets.remove(sessionUser.getId());
    }

    /*
     * 房主创建房间
     */
    private void createRoom(UserEntity owner, String roomName, WebSocketSession socketSession) {
        RoomEntity roomSession = new RoomEntity();
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
        RoomEntity roomSession = rooms.get(roomId);
        roomSession.joinWatch(watchUser);
        // cache 在 socket 列表
        sockets.put(watchUser.getId(), socketSession);
        // cache socket in room
        socketSession.getAttributes().put("roomId", roomSession.getId());
    }
}
