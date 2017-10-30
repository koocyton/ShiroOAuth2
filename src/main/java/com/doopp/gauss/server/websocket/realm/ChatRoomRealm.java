package com.doopp.gauss.server.websocket.realm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.server.websocket.LiveSocketHandler;
import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChatRoomRealm {

    // log
    private final Logger logger = LoggerFactory.getLogger(ChatRoomRealm.class);

    // room's session
    private static final Map<Integer, RoomEntity> roomSessions = new HashMap<>();

    // last created room id
    private static int lastRoomId = 54612;

    @Autowired
    private LiveSocketHandler liveSocketHandler;

    public void handleTextMessage(UserEntity sendUser, RoomEntity roomSession, JSONObject message) {
        // 插入发送人
        message.put("sendUserId", sendUser.getId());
        message.put("sendUserName", sendUser.getNickname());
        // 获取 action
        String messageAction = message.getString("action");
        switch(messageAction) {
            // 房间内聊天
            case "public-talk" :
                this.publicTalk(sendUser, roomSession, message);
                break;

            // 举行活动
            case "hold-activity" :
                this.holdActivity(sendUser, roomSession, message);
                break;

            // 参加活动
            case "join-activities" :
                this.joinActivity(sendUser, roomSession, message);
                break;

            // 活动开始
            case "start-activities" :
                this.startActivity(sendUser, roomSession, message);
                break;

            // 房主踢人
            case "kick-out" :
                this.kickOut(sendUser, roomSession, message);
                break;
        }
    }

    /*
     * 举行活动
     * 房主发起，给当前房间内所有人推送活动发起
     */
    private void holdActivity(UserEntity sendUser, RoomEntity roomSession, JSONObject message) {
        // 如果不是房主
        if (!sendUser.getId().equals(roomSession.getOwner().getId())) {
            return;
        }
        // 如果活动已经举行并且未结束，抛弃消息
        if (false) {
            return;
        }
        // 标记活动类型，标记活动人数
        //  roomSession.setActivityType("werewolf");
        // 举办活动发送到公共区域
        this.publicTalk(sendUser, roomSession, message);
    }

    /*
     * 加入活动
     * 将用户标注为活动准备
     */
    private void joinActivity(UserEntity sendUser, RoomEntity roomSession, JSONObject message) {
        // 如果是狼人杀
        // todo
        //if (activityType.eq) {

        // }
        // 反馈，加入成功还是失败
        if (true) {
            liveSocketHandler.pushMessage(sendUser, message.toJSONString());
            // 加满人员后，自动开始活动，并给活动人员，标注新的 realm flag
            // todo ... 补充逻辑在此
            // 如果加入的人员够数了，就提示开始活动
            if () {
                this.startActivity(sendUser, roomSession, message);
            }
        }
        //
        else {
            liveSocketHandler.pushMessage(sendUser, message.toJSONString());
        }
    }

    /*
     * 活动开始
     * 活动开始后，将参入的活动人员，标注一个 activity realm
     */
    private void startActivity(UserEntity sendUser, RoomEntity roomSession, JSONObject message) {
    }


    /*
     * 房主踢人
     */
    private void kickOut(UserEntity sendUser, RoomEntity roomSession, JSONObject message) {
        // 如果不是房主
        if (!sendUser.getId().equals(roomSession.getOwner().getId())) {
            return;
        }
        // 查询要踢谁
        JSONObject actionData = message.getObject("data", JSONObject.class);
        Long kickOutUserId = Long.valueOf(actionData.getString("kickOutUser"));
        // 分别从前排或围观者中找到这个人，并踢掉
        UserEntity kickOutUser1 = roomSession.getFrontUsers().get(kickOutUserId);
        if (kickOutUser1!=null) {
            liveSocketHandler.closeConnection(sendUser);
        }
        else {
            UserEntity kickOutUser2 = roomSession.getWatchUsers().get(kickOutUserId);
            if (kickOutUser2!=null) {
                liveSocketHandler.closeConnection(sendUser);
            }
        }
    }

    /*
     * 房间内聊天
     */
    private void publicTalk(UserEntity sendUser, RoomEntity roomSession, JSONObject message) {
        // 发送给前排
        Map<Long, UserEntity> frontUsers = roomSession.getFrontUsers();
        for (UserEntity frontUser : frontUsers.values()) {
            liveSocketHandler.pushMessage(frontUser, message.toJSONString());
        }
        // 发送给围观人
        Map<Long, UserEntity> watchUsers = roomSession.getWatchUsers();
        for (UserEntity watchUser : watchUsers.values()) {
            liveSocketHandler.pushMessage(watchUser, message.toJSONString());
        }
        // 发送给房主
        if (roomSession.getOwner()!=null) {
            liveSocketHandler.pushMessage(roomSession.getOwner(), message.toJSONString());
        }
    }

    /*
     * 处理还没加入房间时
     */
    public void sessionJoinRoom(WebSocketSession socketSession, TextMessage message) {
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
                        liveSocketHandler.closeConnection(socketSession);
                    }
                    break;
                // 加入房间
                case "joinRoom":
                    if (!this.joinRoom(currentUser, messageObject, socketSession)) {
                        liveSocketHandler.closeConnection(socketSession);
                    }
                    break;
                // 默认
                default:
                    liveSocketHandler.closeConnection(socketSession);
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
        socketSession.getAttributes().put("socketRule", LiveSocketHandler.CHAT_ROOM_SOCKET_REALM);
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
        socketSession.getAttributes().put("socketRule", LiveSocketHandler.CHAT_ROOM_SOCKET_REALM);
        // 加入到房间
        roomSessions.get(roomId).joinWatch(user);
        return true;
    }

    /*
     * check is join room
     */
    public boolean isJoinRoom(WebSocketSession socketSession) {
        Object isJoinRoom = socketSession.getAttributes().get("roomId");
        return !Objects.equal(isJoinRoom, null);
    }

    /*
     * 离开房间
     */
    public void leaveRoom(UserEntity user, WebSocketSession socketSession) {
        if (isJoinRoom(socketSession)) {
            int roomId = (int) socketSession.getAttributes().remove("roomId");
            RoomEntity roomSession =  roomSessions.get(roomId);
            if (!Objects.equal(roomSession, null)) {
                roomSession.userLeave(user);
                // 删除房间
                if (roomSession.getOwner()==null && roomSession.getFrontUsers().size()==0 && roomSession.getWatchUsers().size()==0) {
                    roomSessions.remove(roomId);
                }
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

    public Map<Integer, RoomEntity> getRoomSessions() {
        return roomSessions;
    }

    public RoomEntity getRoomSession(int roomId) {
        return roomSessions.get(roomId);
    }
}
