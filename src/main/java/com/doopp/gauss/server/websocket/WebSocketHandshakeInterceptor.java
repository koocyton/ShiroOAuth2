package com.doopp.gauss.server.websocket;

import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 *
 * Created by henry on 2017/7/20.
 */
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    // private final RedisSessionHelper redisSessionHelper = new RedisSessionHelper();

    @Autowired
    private CustomShadedJedis sessionRedis;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {


        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        CustomShadedJedis sessionRedis = (CustomShadedJedis) ctx.getBean("sessionRedis");

        // 默认从 url query 里获取 access token
        String accessToken = null;
        String uriQuery = request.getURI().getQuery();
        int beginOffset = uriQuery.indexOf("access-token=");
        if (beginOffset!=-1) {
            beginOffset = beginOffset + 13;
            accessToken = uriQuery.substring(beginOffset, beginOffset + 32);
        }

        logger.info(" >>> sessionRedis " + sessionRedis);

        // 有 token
        if (accessToken!=null) {
            UserEntity currentUser = (UserEntity) sessionRedis.get(accessToken.getBytes());
            // 在会话里加入当前用户信息
            attributes.put("currentUser", currentUser);
            return currentUser!=null && super.beforeHandshake(request, response, wsHandler, attributes);
        }

        // 不能连接
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
