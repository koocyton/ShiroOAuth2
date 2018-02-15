package com.doopp.gauss.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.doopp.gauss.common.defined.Action;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.service.GameService;
import com.doopp.gauss.common.service.PlayerService;
import com.doopp.gauss.common.service.SocketChannelService;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.websockets.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@Service("socketChannelService")
public class SocketChannelServiceImpl implements SocketChannelService {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(SocketChannelServiceImpl.class);

    Map<Long, WebSocketChannel> socketGroup = new HashMap<>();

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    // text message
    @Override
    public void onFullTextMessage(WebSocketChannel socketChannel, BufferedTextMessage message) {
        // Get message
        JSONObject messageObject = JSONObject.parseObject(message.getData());
        if (messageObject==null) {
            return;
        }
        // Get action
        String playerAction = messageObject.getString("action");
        if (playerAction!=null) {
            String dataMessage = messageObject.getString("data");
            JSONObject jsonObject;
            try {
                jsonObject = JSONObject.parseObject(dataMessage);
            }
            catch(Exception e) {
                jsonObject = new JSONObject();
            }
            gameService.actionDispatcher(socketChannel, playerAction, jsonObject);
        }
    }

    @Override
    public void onConnect(WebSocketChannel socketChannel) {
        // 建立连接
        this.socketConnect(socketChannel);
        // 用户建立连接
        playerService.join(socketChannel);
        // 给用户发消息
        Player player = playerService.getPlayer(socketChannel);
        if (player!=null) {
            JSONObject jsonObject = new JSONObject() {{
                put("id", player.getId());
                put("room_id", player.getRoom_id());
            }};
            gameService.sendMessage(player, Action.JOIN_ROOM, jsonObject);
        }
    }

    @Override
    public void onClose(WebSocketChannel socketChannel, StreamSourceFrameChannel channel) {
        // 删除用户
        playerService.leave(socketChannel);
        // 断开连接
        this.socketDisconnect(socketChannel);
    }

    @Override
    public Long getUidByChannel(WebSocketChannel socketChannel) {
        return (Long) socketChannel.getAttribute("userId");
    }

    @Override
    public void sendMessage(Long socketChannelKey, String message) {
        WebSocketChannel socketChannel = socketGroup.get(socketChannelKey);
        if (socketChannel!=null) {
            WebSockets.sendText(message, socketChannel, null);
        }
    }

    // socket 建立连接
    private void socketConnect(WebSocketChannel socketChannel) {
        if (socketChannel!=null) {
            // 索引
            Long userId = this.getUidByChannel(socketChannel);
            // 先断开
            this.socketDisconnect(socketGroup.get(userId));
            // 将连接保存到 socket group 里
            socketGroup.put(userId, socketChannel);
        }
    }

    // socket 断开连接
    private void socketDisconnect(WebSocketChannel socketChannel) {
        if (socketChannel!=null) {
            Long userId = this.getUidByChannel(socketChannel);
            if (socketChannel.isOpen()) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    logger.warn("WARN : User {} socketSession IOException on closing", userId);
                }
            }
            // 从socket组里移除
            socketGroup.remove(userId);
        }
    }
}
