package com.doopp.gauss.server.undertow;

import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.service.AccountService;
import com.doopp.gauss.common.service.SocketChannelService;
import com.doopp.gauss.common.utils.ApplicationContextUtil;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class GameSocketConnectionCallback implements WebSocketConnectionCallback
{
    private final Logger logger = LoggerFactory.getLogger(GameSocketConnectionCallback.class);

    private AccountService accountService = (AccountService) ApplicationContextUtil.getBean("accountService");

    private SocketChannelService socketChannelService = (SocketChannelService) ApplicationContextUtil.getBean("socketChannelService");

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel)
    {
        User user = this.sessionFilter(exchange, channel);
        if (user!=null) {
            // set userId to channel
            channel.setAttribute("userId", user.getId());
            // create connect
            socketChannelService.onConnect(channel);
            // set receive
            channel.getReceiveSetter().set(new GameReceiveListener());
            channel.resumeReceives();
        }
    }

    private User sessionFilter(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        String sessionToken = exchange.getRequestHeader("session-token");
        if (sessionToken==null) {
            List<String> sessionTokens = exchange.getRequestParameters().get("session-token");
            sessionToken = sessionTokens.get(0);
        }
        User user = null;
        if (sessionToken!=null) {
            user = accountService.getCacheUser(sessionToken);
        }
        if (user==null) {
            try {
                channel.close();
            }
            catch(IOException e) {
                logger.info("{}", e.getMessage());
            }
        }
        return user;
    }

    private class GameReceiveListener extends AbstractReceiveListener {

        @Override
        protected void onFullTextMessage(WebSocketChannel socketChannel, BufferedTextMessage message) {
            socketChannelService.onFullTextMessage(socketChannel, message);
        }

        @Override
        protected void onClose(WebSocketChannel socketChannel, StreamSourceFrameChannel channel) throws IOException {
            socketChannelService.onClose(socketChannel, channel);
            super.onClose(socketChannel, channel);
        }

        @Override
        protected void onError(WebSocketChannel socketChannel, Throwable error) {
            socketChannelService.onClose(socketChannel, null);
            super.onError(socketChannel, error);
        }
    }
}
