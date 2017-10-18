package com.doopp.gauss.server.websocket;

import com.doopp.gauss.server.websocket.handler.GameSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
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
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(systemWebSocketHandler(),"/game-socket")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");

        registry.addHandler(systemWebSocketHandler(),"/game-socket/sockjs")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Bean
    public WebSocketHandler systemWebSocketHandler(){
        return new GameSocketHandler();
    }
}
