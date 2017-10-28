package com.doopp.gauss.server.configuration;

import com.doopp.gauss.server.websocket.WebSocketHandshakeInterceptor;
import com.doopp.gauss.server.websocket.LiveSocketHandler;
import com.doopp.gauss.server.websocket.rule.ChatRoomRule;
import com.doopp.gauss.server.websocket.rule.WereWolfRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Web Socket Config
 *
 * Created by henry on 2017/7/20.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(liveSocketHandler(),"/live-socket")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");

        // registry.addHandler(roomSocketHandler(),"/live-socket/sockjs")
        //        .addInterceptors(new WebSocketHandshakeInterceptor())
        //        .setAllowedOrigins("*")
        //        .withSockJS();
    }

    @Bean
    public LiveSocketHandler liveSocketHandler(){
        return new LiveSocketHandler();
    }

    @Bean
    public ChatRoomRule chatRoomRule() {
        return new ChatRoomRule();
    }

    @Bean
    public WereWolfRule wereWolfRule() {
        return new WereWolfRule();
    }
}
