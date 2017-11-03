package com.doopp.gauss.api.game;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.server.task.DaemonMessage;
import org.springframework.web.socket.WebSocketSession;

public interface RoomGame {

    // 游戏管理者发送消息
    void handleDaemonMessage(DaemonMessage daemonMessage);

    // 消息
    void handleTextMessage(WebSocketSession socketSession, RoomEntity sessionRoom, UserEntity sessionUser, JSONObject messageObject);

    // 加入游戏时
    void joinGame();

    // 退出游戏时
    void leaveGame();

    // 获取游戏名
    int getGameType();
}
