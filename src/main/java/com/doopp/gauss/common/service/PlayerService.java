package com.doopp.gauss.common.service;

import com.doopp.gauss.common.entity.Player;
import io.undertow.websockets.core.WebSocketChannel;

public interface PlayerService {

    // 用户建立连接
    void join(WebSocketChannel socketChannel);

    // 用户建立连接
    void join(Long uid);

    // 用户断开连接
    void leave(WebSocketChannel socketChannel);

    // 用户断开连接
    void leave(Long uid);

    // 通过渠道获取用户
    Player getPlayer(WebSocketChannel socketChannel);

    // 通过ID获取用户
    Player getPlayer(Long uid);
}
