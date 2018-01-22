package com.doopp.gauss.common.service;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.Player;

public interface PlayService {

    // 用户发送的命令转发
    void actionDispatcher(Room room, Player player, String action, JSONObject messageObject);

    // 接受用户准备好了的消息
    void readyAction(Room room, Player player);


    // 上行，狼人杀人
    void werewolfAction(Room room, Player player, JSONObject messageObject);

    // 上行，预言家查身份
    void seerAction(Room room, Player player, JSONObject messageObject);

    // 上行，女巫救人或毒杀
    void witchAction(Room room, Player player, JSONObject messageObject);

    // 上行，猎人杀人
    void hunterAction(Room room, Player player, JSONObject messageObject);

    // 发送消息给用户
    void sendMessage(Player player, String message);

    // 发送信息
    void sendMessage(Room room, String message);

    // 延迟一段时间
    void delay(float second);
}
