package com.doopp.gauss.common.service;

import io.undertow.websockets.core.WebSocketChannel;

public interface GameService {


      // 用户发送的命令转发
      void actionDispatcher(WebSocketChannel socketChannel);

//    // 接受用户准备好了的消息
//    void readyAction(Room room, Player player);
//
//
//    // 上行，狼人杀人
//    void werewolfAction(Room room, Player player, JSONObject messageObject);
//
//    // 上行，预言家查身份
//    void seerAction(Room room, Player player, JSONObject messageObject);
//
//    // 上行，女巫救人或毒杀
//    void witchAction(Room room, Player player, JSONObject messageObject);
//
//    // 上行，猎人杀人
//    void hunterAction(Room room, Player player, JSONObject messageObject);
//
//    // 上行，玩家投票
//    void playerVote(Room room, Player actionPlayer, JSONObject messageObject);
//
//    // 发送信息
//    void sendMessage(Player player, String action, Object data);
//    void sendMessage(Player[] players, String action, Object data);
//    void sendMessage(Room room, String action, Object data);
//
//    void sendMessage(Player player, String message);
//    void sendMessage(Player[] players, String message);
//    void sendMessage(Room room, String message);
}
