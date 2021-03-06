package com.doopp.gauss.server.websocket;

import com.doopp.gauss.api.entity.UserEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
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
        //HttpSession httpSession = serverRequest.getServletRequest().getSession(isCreateSession());
        //attributes.put("currentUser", httpSession.getAttribute("currentUser"));

        UserEntity user = (UserEntity) serverRequest.getServletRequest().getAttribute("currentUser");
        attributes.put("currentUser", user);

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
