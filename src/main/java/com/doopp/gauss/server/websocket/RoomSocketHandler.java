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

public class RoomSocketHandler extends AbstractWebSocketHandler {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(RoomSocketHandler.class);

    // socket`s session
    private static final Map<Long, WebSocketSession> sockets = new HashMap<>();

    // room`s session
    private static final Map<Integer, Room> rooms = new HashMap<>();

    // room id
    private static int lastRoomId = 54612;

    // GameTaskDispatcher
    @Autowired
    private GameTaskDispatcher gameTaskDispatcher;

    /*
     * 获取房间列表
     */
    public Map<Integer, Room> getRooms() {
        return rooms;
    }

    // 房间内公共频道说话
    private void roomPublicTalk(Room sessionRoom, JSONObject messageObject) throws IOException {
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

    // 房间内游戏频道说话
    public void roomGameTalk(Room sessionRoom, TextMessage message) {
        // send to game user
        for(Long userId : sessionRoom.getGameUsersId().values()) {
            try {
                sockets.get(userId).sendMessage(message);
            }
            catch(IOException e) {
                logger.warn(" >>> sockets.get(userId).sendMessage(" + message.getPayload() + ")");
            }
        }
    }

    // text message
    @Override
    protected void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws Exception {
        // 在哪个房间
        Room sessionRoom = this.getSessionRoom(socketSession);
        // 是哪个用户
        User sessionUser = this.getSessionUser(socketSession);
        // 校验消息
        JSONObject messageObject = JSONObject.parseObject(message.getPayload());
        // get action
        String action = messageObject.getString("action");

        // 如果 Action 异常
        if (action==null) {
            logger.info(" >>> no action in message ");
            // socketSession.close();
        }

        // 如果没有进入房间
        else if (sessionRoom==null) {
            // logger.info(" >>> null sessionRoom ");
            this.openRoom(socketSession, sessionUser, messageObject);
        }

        // 如果在房间内，并且 action 正常
        else {
            //
            messageObject.put("sendUserId", sessionUser.getId());
            messageObject.put("sendUserName", sessionUser.getNickname());
            // 是否有参加活动
            User joinGameMe = sessionRoom.getGameUser(sessionUser.getId());
            // 活动状态
            Room.GameStatus gameStatus = sessionRoom.getGameStatus();

            // 如果活动正在进行，并且用户加入了游戏
            if (gameStatus.equals(Room.GameStatus.Playing) && joinGameMe!=null) {
                // 将消息发送到当前房间进行的游戏中去
                sessionRoom.getGameTask().roomMessageHandle(messageObject);
            }

            // 房主组局
            else if (action.equals("callPlayer")) {
                // 房主在房间 的 非游戏时间才能征集游戏玩家
                if (!gameStatus.equals(Room.GameStatus.Playing) && sessionUser.getId().equals(sessionRoom.getOwner().getId())) {
                    int gameType = messageObject.getInteger("gameType");
                    switch(gameType) {
                        case Room.WERE_WOLF_GAME :
                            sessionRoom.setGameType(Room.WERE_WOLF_GAME);
                            break;
                        case Room.BATTLE_ROYALE_GAME :
                            sessionRoom.setGameType(Room.WERE_WOLF_GAME);
                            break;
                        case Room.GUESS_DRAW_GAME :
                            sessionRoom.setGameType(Room.WERE_WOLF_GAME);
                            break;
                        default:
                            return;
                    }
                    // set game status
                    sessionRoom.setGameStatus(Room.GameStatus.Calling);
                    // send message
                    this.roomPublicTalk(sessionRoom, messageObject);
                    // return
                    messageObject.put("result", true);
                    socketSession.sendMessage(new TextMessage(messageObject.toJSONString()));
                }
            }

            // 玩家参与
            else if (action.equals("joinGame")) {
                // 召集阶段，玩家才能申请参与
                if (gameStatus.equals(Room.GameStatus.Calling) && sessionRoom.playerNumber()<=4) {
                    // join game
                    sessionRoom.joinGame(sessionUser);
                    //
                    TextMessage messageText = new TextMessage(messageObject.toJSONString());
                    // return
                    messageObject.put("result", true);
                    // socketSession.sendMessage(messageText);
                    roomGameTalk(sessionRoom, messageText);
                }

                // 人够了就开始游戏
                if (gameStatus.equals(Room.GameStatus.Calling) && sessionRoom.playerNumber()>=4) {
                    // 提示游戏参与者开始游戏
                    TextMessage textMessage = new TextMessage("{action:\"gameStart\", gameType:\"" + sessionRoom.getGameType() + "\"}");
                    // 发送给游戏参与者
                    this.roomGameTalk(sessionRoom, textMessage);
                    sessionRoom.setGameStatus(Room.GameStatus.Playing);
                    // 启用一个新线程来执行队列
                    gameTaskDispatcher.execute(sessionRoom.getGameType(), sessionRoom);
                }
            }

            // 玩家退出组局
            else if (action.equals("leaveGame")) {
                if (gameStatus.equals(Room.GameStatus.Calling)) {
                    // return
                    messageObject.put("result", true);
                    socketSession.sendMessage(new TextMessage(messageObject.toJSONString()));
                    sessionRoom.leaveGame(sessionUser);
                }
            }

            // 没有活动，或这个用户没有加入游戏
            else if (action.equals("publicTalk")) {
                this.roomPublicTalk(sessionRoom, messageObject);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession socketSession, CloseStatus status) throws Exception {
        // 关闭连接时，离开房间
        this.leaveRoom(socketSession);
    }

    // 设置房间
    private void setSessionRoom(WebSocketSession socketSession, User sessionRoom) {
        socketSession.getAttributes().put("sessionRoomId", sessionRoom.getId());
        rooms.put(sessionRoom.getId(), sessionRoom);
    }

    // 获取房间
    private Room getSessionRoom(WebSocketSession socketSession) {
        int roomId = (int) socketSession.getAttributes().get("sessionRoomId");
        return roomId==0 ? null : rooms.get(roomId);
    }

    // 获取用户
    private User getSessionUser(WebSocketSession socketSession) {
        return (User) socketSession.getAttributes().get("sessionUser");
    }

    // 开房
    private void openRoom(WebSocketSession socketSession, User sessionUser, JSONObject messageObject) throws Exception {
        // get action
        String action = messageObject.getString("action");
        // 创建房间
        if (action.equals("createRoom")) {
            // 房间名
            String roomName = messageObject.getString("roomName");
            if (roomName!=null) {
                this.createRoom(sessionUser, roomName, socketSession);
                // 将结果放回
                messageObject.put("result", true);
                socketSession.sendMessage(new TextMessage(messageObject.toJSONString()));
                return;
            }
        }
        // 加入到房间
        else if (action.equals("joinRoom")) {
            // 房间 id
            Integer joinRoomId = messageObject.getInteger("roomId");
            if (joinRoomId!=null) {
                if (this.joinRoom(sessionUser, joinRoomId, socketSession)) {
                    // 将结果放回
                    messageObject.put("result", true);
                    socketSession.sendMessage(new TextMessage(messageObject.toJSONString()));
                }
                return;
            }
        }
        socketSession.close();
    }

    // 离开房间
    private void leaveRoom(WebSocketSession socketSession) {
        Room sessionRoom = this.getSessionRoom(socketSession);
        User sessionUser = this.getSessionUser(socketSession);
        if (sessionRoom!=null) {
            // 退出房间
            sessionRoom.userLeave(sessionUser);
            // logger.info(" >>> " + sessionRoom.getId() + " : " + sessionRoom.getWatchUsers().size() + " : " + sessionRoom.getOwner());
            if (sessionRoom.getWatchUsers().size() == 0 && sessionRoom.getOwner() == null) {
                rooms.remove(sessionRoom.getId());
                sessionRoom.setGameTask(null);
            }
        }
        // 关闭链接
        sockets.remove(sessionUser.getId());
    }

    // 房主创建房间
    private void createRoom(User owner, String roomName, WebSocketSession socketSession) {
        Room roomSession = new Room();
        synchronized ("createChatRoom") {
            roomSession.setId(++lastRoomId);
        }
        roomSession.setName(roomName);
        roomSession.setOwner(owner);
        // 保存在房间列表
        this.setSessionRoom(socketSession, roomSession);
        // rooms.put(roomSession.getId(), roomSession);
        // cache 在 socket 列表
        sockets.put(owner.getId(), socketSession);
        // cache socket in room
        socketSession.getAttributes().put("roomId", roomSession.getId());
    }

    // 用户加入房间，普通状态
    private boolean joinRoom(User watchUser, int roomId, WebSocketSession socketSession) {
        Room roomSession = rooms.get(roomId);
        // logger.info(" >> " + roomSession + " >> " + roomSession.getSeatCount() + " >> " + roomSession.getWatchUsers().size());
        if (roomSession!=null && roomSession.getSeatCount()>roomSession.getWatchUsers().size()) {
            //
            roomSession.joinWatch(watchUser);
            // 保存在房间列表
            this.setSessionRoom(socketSession, roomSession);
            // cache 在 socket 列表
            sockets.put(watchUser.getId(), socketSession);
            // cache socket in room
            socketSession.getAttributes().put("roomId", roomSession.getId());
            //
            return true;
        }
        return false;
    }

    // 获取一个空的房间号
    public int getLastRoomId() {
        return lastRoomId;
    }
}
