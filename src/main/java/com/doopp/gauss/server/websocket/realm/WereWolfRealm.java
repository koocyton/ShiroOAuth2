package com.doopp.gauss.server.websocket.realm;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class WereWolfRealm {

    public void handleTextMessage(UserEntity currentUser, RoomEntity roomSession, JSONObject message) {

    }
}
