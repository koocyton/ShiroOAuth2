package com.doopp.gauss.common.service;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.Room;
import io.undertow.websockets.core.WebSocketChannel;

public interface GameService {

      // 用户发送的命令转发
      void actionDispatcher(WebSocketChannel socketChannel, String playerAction, JSONObject actionData);

      // 发送信息
      void sendMessage(Player player, String action, Object data);
      void sendMessage(Player player, String message);

      void sendMessage(Player[] players, String action, Object data);
      void sendMessage(Player[] players, String message);

      void sendMessage(Room room, String action, Object data);
      void sendMessage(Room room, String message);
}
