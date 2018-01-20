package com.doopp.gauss.common.service;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;

public interface PlayService {

    // 用户发送的命令转发
    void actionDispatcher(Room room, User user, String action, JSONObject messageObject);

    // 接受用户准备好了的消息
    void readyAction(Room room, User user);

    // 上行，狼人杀人
    void werewolfAction(Room room, User user);

    // 上行，预言家查身份
    void seerAction(Room room, User user);

    // 上行，女巫救人或毒杀
    void witchAction(Room room, User user);


    // 上行，猎人杀人
    void hunterAction(Room room, User user);

    // 下发，结果
    void sendResults(Room room, String message);

    // 下发
    void callGameOver(Room room);
}
