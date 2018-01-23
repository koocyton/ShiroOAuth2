package com.doopp.gauss.server.websocket;

import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.mapper.UserMapper;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 *
 * Created by henry on 2017/10/19.
 */
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        // 将用户信息带入到 socket 里面
        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
        User user = (User) serverRequest.getServletRequest().getAttribute("sessionUser");
        Player player = UserMapper.INSTANCE.userToPlayer(user);
        player.setRoomId(0);
        // init session user
        attributes.put("sessionPlayer", player);

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
