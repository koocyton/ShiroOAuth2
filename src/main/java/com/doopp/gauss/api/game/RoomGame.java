package com.doopp.gauss.api.game;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import org.springframework.web.socket.WebSocketSession;

public interface RoomGame {

    // 消息
    void handleTextMessage(WebSocketSession socketSession, RoomEntity theRoom, UserEntity sendUser, JSONObject message);

    // 加入游戏时
    void joinGame();

    // 退出游戏时
    void leaveGame();
}
