package com.doopp.gauss.common.service;

import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;

public interface SocketChannelService {

    void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message);

    void onConnect(WebSocketChannel channel);

    void onClose(WebSocketChannel socketChannel, StreamSourceFrameChannel channel);

    Long getUidByChannel(WebSocketChannel channel);

    void sendMessage(Long socketChannelKey, String message);
}
