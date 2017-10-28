package com.doopp.gauss.server.websocket.rule;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.server.websocket.LiveSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class ChatRoomRule {

    @Autowired
    private LiveSocketHandler liveSocketHandler;

    public void handleTextMessage(UserEntity sendUser, RoomEntity roomSession, JSONObject message) {
        // 插入发送人
        message.put("sendUser", sendUser.getId());
        message.put("sendUserName", sendUser.getNickname());
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
}
