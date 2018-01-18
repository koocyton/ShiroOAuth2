package com.doopp.gauss.common.service;

import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;

public interface PlayService {

    // 用户发送的命令转发
    void actionDispatcher();

    // 接受用户准备好了的消息
    void readyAction(Room room, User user);

    // 所有游戏准备好了后，游戏开始
    void gameStart(Room room);


    // 先随机派发用户身份
    void distributeIdentity(Room room);

    // 下发，进入夜晚
    void enterNight(Room room);

    // 下发，进入白天
    void enterDay(Room room);


    // 下发，狼人出来杀人
    void callWerewolf(Room room);

    // 上行，狼人杀人
    void werewolfAction(Room room, User user);


    // 下发，预言家查身份
    void callSeer(Room room);

    // 上行，预言家查身份
    void seerAction(Room room, User user);


    // 下发，女巫救人或毒杀
    void callWitch(Room room);

    // 上行，女巫救人或毒杀
    void witchAction(Room room, User user);


    // 下发，猎人杀人
    void callHunter(Room room);

    // 上行，猎人杀人
    void hunterAction(Room room, User user);


    // 下发，结果
    void sendResults(Room room, String message);


    // 下发，开始轮流发言
    void callSpeak(Room room);
}
