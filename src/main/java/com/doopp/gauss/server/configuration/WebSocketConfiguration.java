package com.doopp.gauss.server.configuration;

import com.doopp.gauss.server.websocket.WebSocketHandshakeInterceptor;
import com.doopp.gauss.server.websocket.handler.RoomSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
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

        registry.addHandler(roomSocketHandler(),"/room-socket")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");

        // registry.addHandler(roomSocketHandler(),"/game-socket/sockjs")
        //        .addInterceptors(new WebSocketHandshakeInterceptor())
        //        .setAllowedOrigins("*")
        //        .withSockJS();
    }

    @Bean
    public WebSocketHandler roomSocketHandler(){
        return new RoomSocketHandler();
    }
}
